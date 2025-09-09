package com.example.group_chat.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessLoginResponse(
    @SerialName("id")
    val id:String,
    @SerialName("token")
    val token:String?=null,
    @SerialName("email")
    val email:String,
    @SerialName("username")
    val username:String,
    @SerialName("firstName")
    val firstName:String? = null,
    @SerialName("secondName")
    val secondName:String? = null,
    @SerialName("avatar")
    val avatar:String? = null,
    @SerialName("exp")
    val exp:String
)
