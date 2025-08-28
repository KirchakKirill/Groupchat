package com.example.group_chat.domain.model

data class MessageModel(
    val chatId: String,
    val senderId: String,
    val username:String,
    val contentType:String,
    val content:String,
    val mediaContent: String?
)

data class ContentReceive(
    val contentType: String,
    val content: String,
    val mediaContent: String? = null
)

