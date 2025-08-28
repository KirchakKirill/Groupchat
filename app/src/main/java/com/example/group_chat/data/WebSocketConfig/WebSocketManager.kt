package com.example.group_chat.data.WebSocketConfig

import android.util.Log
import com.example.group_chat.domain.model.MessageModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketManager(
    private  val okHttpClient: OkHttpClient,
    private val  gson: Gson
)
{
    private var webSocket:WebSocket? = null

    fun openSocketConnection(url:String,token:String,chatId:String,nameUser:String): Flow<WebSocketMessageType>  = callbackFlow {

        val headers = Headers.Builder()
            .add("Authorization", "Bearer $token")
            .build()
        val urlWithChatIDAndUserName = "$url/${chatId}?username=$nameUser"

        val request = Request.Builder()
            .url(urlWithChatIDAndUserName)
            .headers(headers = headers)
            .build()

        val listener = MessageWebSocketListener(
            scope = this,
            onSocketAssigned = {ws -> webSocket = ws},
            gson = gson
        )

        webSocket = okHttpClient.newWebSocket(request,listener)

        awaitClose {
            webSocket?.close(1000,"Flow canceled")
        }
    }.flowOn(Dispatchers.IO)

    fun send(text: String): Boolean {
        val result = webSocket?.send(text) ?: false
        Log.d("WebSocket", "Send message result: $result, content: $text")
        return result
    }

    fun close(reason:String) = webSocket?.close(1000,reason)
}

sealed class WebSocketMessageType {
    data class UserMessage(val data: MessageModel) : WebSocketMessageType()
    data class SystemMessage(val text: String) : WebSocketMessageType()
    data class MessageError(val error:Throwable): WebSocketMessageType()
}