package com.example.newsapp.article.di

import com.example.newsapp.article.presentation.ArticleViewModel
import com.example.newsapp.news.presentation.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val articleModule = module {
    viewModel{ ArticleViewModel(get()) }
}