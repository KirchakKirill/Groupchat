package com.example.group_chat.domain.model

data class LoginResponseModel(
    val id:String,
    val token:String? = null,
    val email:String,
    val username:String,
    val firstName:String? = null,
    val secondName:String? = null,
    val avatar:String? = null,
    val exp:String
)