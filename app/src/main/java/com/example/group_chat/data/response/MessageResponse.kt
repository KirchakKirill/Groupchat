package com.example.group_chat.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("chatId") val chatId: String? = null,
    @SerialName("senderId") val senderId: String? = null,
    @SerialName("username") val username:String? = null,
    @SerialName("contentType") val contentType:String? = null,
    @SerialName("content") val content:String? = null,
    @SerialName("mediaContent")val mediaContent: String? = null
)