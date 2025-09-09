package com.example.group_chat.domain.model

data class UserChatRolesModel(
    val id:String,
    val chatId:String,
    val userId:String,
    val role:String,
    val joinedAt:String
)