package com.example.group_chat.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("usernameOrEmail")
    val usernameOrEmail:String,
    @SerialName("password")
    val password:String
)