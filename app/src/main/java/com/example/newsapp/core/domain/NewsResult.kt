package com.example.newsapp.core.domain

sealed class NewsResult<T>(
    val data: T ? = null,
    val error:String?
){
    class Success<T>(data: T?):NewsResult<T>(data, null)
    class Error<T>(error: String?):NewsResult<T>(null,error)
}