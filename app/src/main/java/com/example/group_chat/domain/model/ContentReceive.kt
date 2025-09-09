package com.example.group_chat.domain.model

data class ContentReceive(
    val contentType: String,
    val content: String,
    val mediaContent: String? = null
)