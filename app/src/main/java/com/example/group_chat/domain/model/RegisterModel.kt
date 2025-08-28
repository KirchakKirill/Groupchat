package com.example.group_chat.domain.model

data class RegisterModel(
    val email:String ="",
    val password:String= "",
    val username:String = "",
    val firstName:String = "",
    val secondName:String = "",
    val avatar:String = ""
)
