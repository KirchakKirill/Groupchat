package com.example.group_chat.domain.model



data class MessageModel(
    val id:String,
    val chatId: String,
    val senderId: String,
    val username:String,
    val contentType:String,
    val content:String,
    val mediaContent: String?,
    val createdAt:String,
)





