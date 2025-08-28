package com.example.group_chat.data.dataSource

import com.example.group_chat.data.response.ChatResponse
import com.example.group_chat.data.service.ChatService
import retrofit2.Response
class ChatDataSource(
    private val chatService: ChatService
) {
    suspend fun createChatRoom(chatName:String,authHeader:String):Response<ChatResponse> = chatService.createChatRoom(chatName,authHeader)
    suspend fun followChat(chatId:String,authHeader:String):Response<Unit> = chatService.followChat(chatId,authHeader)
    suspend fun getChatsAll(authHeader:String):Response<List<ChatResponse>> = chatService.getChatsAll(authHeader)
    suspend fun getChatsByUser(authHeader:String):Response<List<ChatResponse>> = chatService.getChatsByUser(authHeader)

}