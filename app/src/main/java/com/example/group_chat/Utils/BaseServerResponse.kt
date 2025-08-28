package com.example.group_chat.Utils

import retrofit2.Response

abstract class BaseServerResponse
{
    suspend fun <T> safeServerCall(call: suspend() -> Response<T>):NetworkResult<T>{
            try {
                val response = call.invoke()
                if (response.isSuccessful)
                {
                    val body  = response.body()
                    body?.let {
                        return NetworkResult.Success(it)
                    } ?: return  errorMessage("Body is not valid")

                }
                else
                {
                    return errorMessage("Code:${response.code()},Message:${response.message()}")
                }
            }
            catch (e:Exception)
            {
                return errorMessage(e.message)
            }
    }

    private fun <T> errorMessage(message:String?) = NetworkResult.Error<T>(data = null, message = "Server call failed: $message")
}