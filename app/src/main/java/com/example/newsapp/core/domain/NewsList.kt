package com.example.newsapp.core.domain

import kotlinx.serialization.Serializable

data class NewsList(
    val nextPage: String?,
    val articles: List<Article>?,
)