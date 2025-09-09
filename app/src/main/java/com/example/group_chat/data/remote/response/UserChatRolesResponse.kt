package com.example.group_chat.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserChatRolesResponse(
    @SerialName("id")
    val id:String?=null,
    @SerialName("userId")
    val userId:String?=null,
    @SerialName("chatId")
    val chatId:String?=null,
    @SerialName("role")
    val role:String?=null,
    @SerialName("joinedAt")
    val joinedAt:String?=null,
)