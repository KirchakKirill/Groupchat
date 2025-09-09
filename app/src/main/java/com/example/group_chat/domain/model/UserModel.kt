package com.example.group_chat.domain.model

data class UserModel(
    val id:String,
    val email:String? = null,
    val username:String,
    val firstName:String? = null,
    val secondName:String? = null,
    val avatar:String? = null
)