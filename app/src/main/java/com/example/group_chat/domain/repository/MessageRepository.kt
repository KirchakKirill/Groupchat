package com.example.group_chat.domain.repository

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.response.MessageResponse

interface MessageRepository
{
    suspend fun  getAllMessages(page:String): NetworkResult<List<MessageResponse>>

    suspend fun  getAllMessageByType(page: String,type:String,chatId:String): NetworkResult<List<MessageResponse>>

    suspend fun  getAllMessageByChat(page: String,chatId:String): NetworkResult<List<MessageResponse>>

    suspend fun  getAllMessageByUser(page: String,chatId:String,userId:String): NetworkResult<List<MessageResponse>>
}