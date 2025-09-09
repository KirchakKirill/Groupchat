package com.example.group_chat.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    @SerialName("id")
    val id:String? = null,
    @SerialName("name")
    val name:String? = null,
    @SerialName("createdAt")
    val createdAt:String? = null
)