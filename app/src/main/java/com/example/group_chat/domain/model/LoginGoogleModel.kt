package com.example.group_chat.domain.model

data class LoginGoogleModel(
    val token:String,
    val email:String,
    val exp:String,
)
