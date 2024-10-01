package com.example.myapplication

data class Post(
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val userIconUrl: String = "",  // 新しく追加
    val timestamp: Long = 0
)



