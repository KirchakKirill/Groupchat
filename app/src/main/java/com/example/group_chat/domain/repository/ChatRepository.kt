package com.example.group_chat.domain.repository

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.response.ChatResponse
import com.example.group_chat.domain.model.ChatModel

interface ChatRepository
{
    suspend fun  getChatsAll(authHeader:String):NetworkResult<List<ChatResponse>>
    suspend fun  createChatRoom(chatName:String,authHeader:String):NetworkResult<ChatResponse>
    suspend fun followChat(chatId:String,authHeader:String):NetworkResult<Unit>
    suspend fun getChatsByUser(authHeader:String):NetworkResult<List<ChatResponse>>
}