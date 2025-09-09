package com.example.group_chat.data.remote.response

import android.graphics.Picture
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("token")
    val token:String? = null,
    @SerialName("email")
    val email:String?=null,
    @SerialName("exp")
    val exp:String? = null
)