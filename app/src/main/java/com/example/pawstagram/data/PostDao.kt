package com.example.pawstagram.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestampEpochMillis DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Update
    suspend fun update(post: PostEntity)

    @Query("UPDATE posts SET liked = NOT liked, likeCount = CASE WHEN liked THEN likeCount - 1 ELSE likeCount + 1 END WHERE id = :postId")
    suspend fun toggleLike(postId: Long)
}



