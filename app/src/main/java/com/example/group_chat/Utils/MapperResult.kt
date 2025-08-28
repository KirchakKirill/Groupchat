package com.example.group_chat.Utils

sealed class MapperResult<T>(
    val data: T? = null,
    val message: String? =  null
)
{
    class Success<T>(data: T?):MapperResult<T>(data)
    class Error<T>(data: T?,message: String?):MapperResult<T>(data,message)
}