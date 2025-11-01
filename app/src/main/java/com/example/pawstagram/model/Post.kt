package com.example.pawstagram.model

data class Post(
    val docId: String,
    val id: Long, // stable Long for Lazy list keys
    val imageUri: String,
    val caption: String,
    val hashtags: List<String>,
    val username: String,
    val timestampEpochMillis: Long,
    val likeCount: Int,
    val liked: Boolean
)


