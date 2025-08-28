package com.example.group_chat.data.response

import android.graphics.Picture
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("token")
    val token:String? = null,
    @SerialName("email")
    val email:String?=null,
    @SerialName("name")
    val name:String? =null,
    @SerialName("givenName")
    val givenName:String? = null,
    @SerialName("familyName")
    val familyName:String? = null,
    @SerialName("picture")
    val picture: String? = null,
    @SerialName("exp")
    val exp:String? = null
)