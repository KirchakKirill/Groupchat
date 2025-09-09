package com.example.group_chat.Utils

sealed class FlowState<out T>
{
    data object Loading:FlowState<Nothing>()
    data class Success<out T>(val data:T?):FlowState<T>()
    data class Error<out T>(val data: T?,val error: Throwable?):FlowState<T>()
}