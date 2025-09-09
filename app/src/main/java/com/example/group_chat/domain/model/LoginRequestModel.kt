package com.example.group_chat.domain.model

data class LoginRequestModel(
    val usernameOrEmail:String,
    val password:String
)