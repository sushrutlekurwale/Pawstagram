package com.example.pawstagram.data.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.pawstagram.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
// Firebase Storage removed; using Cloudinary via OkHttp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

class FirebasePostRepository(
    private val context: Context,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val postsCollection get() = db.collection("posts")

    val posts: Flow<List<Post>> = callbackFlow {
        val uid = auth.currentUser?.uid
        val registration = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("FirebasePostRepository", "Error fetching posts", err)
                    if (err is FirebaseFirestoreException && err.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                        Log.e("FirebasePostRepository", "Firestore index required. Check Firebase Console for index creation link.")
                    }
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                
                val list = snap?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val imageUrl = doc.getString("imageUrl") ?: return@mapNotNull null
                        val caption = doc.getString("caption") ?: ""
                        val hashtags = (doc.get("hashtags") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                        val username = doc.getString("username") ?: "user"
                        val timestamp = (doc.getTimestamp("timestamp") ?: Timestamp.now()).toDate().time
                        val likedBy = (doc.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                        val likeCount = likedBy.size
                        val liked = if (uid != null) likedBy.contains(uid) else false
                        Post(
                            docId = id,
                            id = id.hashCode().toLong(),
                            imageUri = imageUrl,
                            caption = caption,
                            hashtags = hashtags,
                            username = username,
                            timestampEpochMillis = timestamp,
                            likeCount = likeCount,
                            liked = liked
                        )
                    } catch (e: Exception) {
                        Log.e("FirebasePostRepository", "Error parsing post document ${doc.id}", e)
                        null
                    }
                } ?: emptyList()
                
                Log.d("FirebasePostRepository", "Fetched ${list.size} posts")
                trySend(list).isSuccess
            }
        awaitClose { registration.remove() }
    }

    suspend fun addPost(imageUri: String, caption: String, hashtags: List<String>, username: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not signed in")
        val localUri = Uri.parse(imageUri)
        val bytes = withContext(Dispatchers.IO) {
            val input = context.contentResolver.openInputStream(localUri)
                ?: throw IllegalArgumentException("Cannot open image")
            try {
                input.readBytes()
            } finally {
                input.close()
            }
        }

        // Upload to Cloudinary (unsigned upload)
        val cloudName = context.getString(com.example.pawstagram.R.string.cloudinary_cloud_name)
        val uploadPreset = context.getString(com.example.pawstagram.R.string.cloudinary_upload_preset)
        val downloadUrl = uploadToCloudinary(
            cloudName = cloudName,
            uploadPreset = uploadPreset,
            fileName = "${uid}_${System.currentTimeMillis()}.jpg",
            bytes = bytes
        )

        val data = hashMapOf(
            "imageUrl" to downloadUrl,
            "caption" to caption,
            "hashtags" to hashtags,
            "username" to username,
            "uid" to uid,
            "timestamp" to Timestamp.now(),
            "likedBy" to emptyList<String>()
        )
        postsCollection.add(data).await()
    }

    suspend fun toggleLike(postDocId: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = postsCollection.document(postDocId)
        db.runTransaction { tr ->
            val snap = tr.get(docRef)
            val likedBy = (snap.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            val isLiked = likedBy.contains(uid)
            if (isLiked) tr.update(docRef, mapOf("likedBy" to FieldValue.arrayRemove(uid)))
            else tr.update(docRef, mapOf("likedBy" to FieldValue.arrayUnion(uid)))
        }.await()
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) cont.resume(task.result, null)
            else cont.resumeWithException(task.exception ?: RuntimeException("Task failed"))
        }
    }
}

private fun buildOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
}

private suspend fun uploadToCloudinary(
    cloudName: String,
    uploadPreset: String,
    fileName: String,
    bytes: ByteArray
): String {
    require(cloudName.isNotBlank() && cloudName != "YOUR_CLOUD_NAME") { "Set Cloudinary cloud name in strings.xml" }
    require(uploadPreset.isNotBlank() && uploadPreset != "YOUR_UNSIGNED_UPLOAD_PRESET") { "Set Cloudinary upload preset in strings.xml" }

    return withContext(Dispatchers.IO) {
        val client = buildOkHttpClient()
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val fileBody = RequestBody.create(mediaType, bytes)

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", fileName, fileBody)
            .addFormDataPart("upload_preset", uploadPreset)
            .addFormDataPart("folder", "pawstagram/images")
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/${cloudName}/image/upload")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IllegalStateException("Cloudinary upload failed: ${response.code}")
            val bodyString = response.body?.string() ?: throw IllegalStateException("Empty response from Cloudinary")
            val json = JSONObject(bodyString)
            json.getString("secure_url")
        }
    }
}


