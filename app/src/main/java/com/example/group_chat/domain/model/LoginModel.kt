package com.example.group_chat.domain.model

data class LoginRequestModel(
    val usernameOrEmail:String,
    val password:String
)

data class LoginResponseModel(
    val id:String,
    val token:String? = null,
    val email:String,
    val username:String,
    val firstName:String? = null,
    val secondName:String? = null,
    val avatar:String? = null
)