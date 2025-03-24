package com.example.newsapp.article.presentation

import com.example.newsapp.core.domain.Article

data class ArticleState(
    val article: Article? = null,
    val isLoading : Boolean = false,
    val isError : Boolean = false,
)
