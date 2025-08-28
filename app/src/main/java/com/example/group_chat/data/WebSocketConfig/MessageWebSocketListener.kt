package com.example.group_chat.data.WebSocketConfig
import android.util.Log
import com.example.group_chat.domain.model.MessageModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.channels.ProducerScope
import okhttp3.Response
import okhttp3.WebSocket
import  okhttp3.WebSocketListener


class MessageWebSocketListener(
    private val scope:ProducerScope<WebSocketMessageType>,
    private val onSocketAssigned: (WebSocket) -> Unit,
    private val gson: Gson
)
    : WebSocketListener()
{
    override fun onOpen(webSocket: WebSocket, response: Response) {
        onSocketAssigned(webSocket)
    }

    override fun onMessage(webSocket: WebSocket, text: String)
    {
        Log.d("WebSocket", "Raw message: $text")

        try {
            val parsedMessage: WebSocketMessageType = parseMessage(text)
            scope.trySend(parsedMessage)
        }
        catch (e:Exception){
            Log.e("Message_Error", e.message ?: "Error in onMessage")
            scope.close(e)
        }
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Connection failed: ${t.message}", t)
        scope.close(t)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Connection closed: code=$code, reason=$reason")
        scope.close()
    }

    private fun parseMessage(text:String): WebSocketMessageType
    {
        return try {
            val jsonObject  = JsonParser().parse(text).asJsonObject
            val messageModel  = MessageModel(
                chatId = jsonObject.get("chatId").asString,
                senderId = jsonObject.get("senderId").asString,
                username = jsonObject.get("username").asString,
                contentType = jsonObject.get("contentType").asString,
                content = jsonObject.get("content")?.asString ?: "",
                mediaContent = jsonObject.get("mediaContent")?.takeIf { !it.isJsonNull }?.asString
            )
            WebSocketMessageType.UserMessage(messageModel)
        }
        catch (e:JsonSyntaxException){
            WebSocketMessageType.SystemMessage(text)
        }


    }
}