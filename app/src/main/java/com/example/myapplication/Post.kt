package com.example.myapplication

data class Post(
    val content: String = "",   // 投稿の内容
    val imageUrl: String = "",  // 画像のURL
    val timestamp: Long = 0     // タイムスタンプ
)

