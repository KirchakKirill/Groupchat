package com.example.group_chat.Utils

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class BaseServerResponse
{
    suspend fun <T> safeServerCall(call: suspend() -> Response<T>):NetworkResult<T>{
            try {
                val response = call.invoke()
                when {
                    response.isSuccessful -> {

                        val body = response.body()

                        body?.let {
                            return NetworkResult.Success(it)
                        } ?: return errorMessage("Body is not valid",response.code())
                    }
                    response.code() == 400 -> {
                        val errorMessage = parseErrorStatus(response)

                        return errorMessage<T>(message = errorMessage ?: "Bad request", errorCode = 400)
                    }
                    response.code() == 401 -> {
                        return errorMessage<T>(message = "Unauthorized",
                            errorCode = 401)
                    }
                    response.code() ==403 -> {
                        return errorMessage<T>(message = "Forbidden",
                            errorCode = 403)
                    }
                    response.code() ==404 -> {
                        return errorMessage<T>(message = "Not Found",
                            errorCode = 404)
                    }

                    response.code() in 500..599 -> {
                        return errorMessage<T>(message = "Server error}",
                            errorCode = response.code())
                    }
                    else -> {
                        return errorMessage<T>(message = "Unknown error: ${response.message()}",
                            errorCode = response.code())
                    }
                }

            }
            catch (e:Exception)
            {
                return errorMessage("Network error: ${e.message}",null)
            }
    }

    private fun <T> errorMessage(message:String?,errorCode:Int?) = NetworkResult.Error<T>(data = null, message = "Server call failed: $message",errorCode)
}

private fun <T> parseErrorStatus(response: Response<T>):String?{
    return try {
        val body = response.errorBody()?.string()
        try {
            val json  = JSONObject(body?:"")
            json.optString("message",json.optString("error","Unknown error"))
        }
        catch (e:JSONException){
            body ?: response.message()
        }

    }
    catch (e:Exception){
        response.message()
    }

}