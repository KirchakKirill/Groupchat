package com.example.group_chat.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistryRequest(
    @SerialName("username")
    val username:String,
    @SerialName("email")
    val email:String,
    @SerialName("password")
    val password:String,
    @SerialName("firstName")
    val firstName:String? = null,
    @SerialName("secondName")
    val secondName:String? = null,
    @SerialName("avatar")
    val avatar:String? = null
)
