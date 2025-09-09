package com.example.group_chat.Utils

sealed class NetworkResult<T>(
    val data:T? = null,
    val message:String? = null,
    val errorCode:Int? = null
)
{
    class Success<T>(data: T?):NetworkResult<T>(data)
    class Error<T>(data: T?,message: String?,errorCode: Int?):NetworkResult<T>(data,message,errorCode)
    class Loading<T>():NetworkResult<T>()
}