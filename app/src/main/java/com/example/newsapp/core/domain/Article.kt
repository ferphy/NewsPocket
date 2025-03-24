package com.example.newsapp.core.domain

import kotlinx.serialization.Serializable

data class Article(
    val articleId: String?,
    val title: String?,
    val description: String?,
    val content: String?,
    val pubDate: String?,
    val sourceName: String?,
    val imageUrl: String?
)