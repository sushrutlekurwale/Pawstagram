package com.example.pawstagram.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawstagram.data.firebase.FirebasePostRepository
import com.example.pawstagram.model.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebasePostRepository(application)

    val posts: StateFlow<List<Post>> = repository.posts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLike(docId: String) {
        viewModelScope.launch { repository.toggleLike(docId) }
    }

    fun addPost(imageUri: String, caption: String, hashtags: List<String>, username: String) {
        viewModelScope.launch {
            repository.addPost(imageUri, caption, hashtags, username)
        }
    }
}


