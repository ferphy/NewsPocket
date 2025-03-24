package com.example.newsapp.news.presentation

import com.example.newsapp.core.domain.Article

data class NewsState(
    val articlesList: List<Article> = emptyList(),
    val nextPage : String?= null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,

)