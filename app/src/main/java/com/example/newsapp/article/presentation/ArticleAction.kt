package com.example.newsapp.article.presentation

interface ArticleAction {
    data class LoadArticle(val articleId: String) : ArticleAction
}