package com.example.group_chat.data.remote.dataSource

import com.example.group_chat.data.remote.response.ChatResponse
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import com.example.group_chat.data.remote.service.ChatService
import retrofit2.Response

class ChatDataSource(
    private val chatService: ChatService
) {
    suspend fun createChatRoom(chatName:String,authHeader:String):Response<UserChatRolesResponse> = chatService.createChatRoom(chatName,authHeader)
    suspend fun followChat(chatId:String,authHeader:String):Response<UserChatRolesResponse> = chatService.followChat(chatId,authHeader)
    suspend fun getChatsAll(authHeader:String):Response<List<ChatResponse>> = chatService.getChatsAll(authHeader)
    suspend fun getChatsByUser(authHeader:String):Response<List<ChatResponse>> = chatService.getChatsByUser(authHeader)
    suspend fun  getChatsPart(authHeader: String,offset:String,limit:String) = chatService.getChatsPart(authHeader,offset,limit)
    suspend fun checkFollowsChat( authHeader:String, chatIds:List<String>):Response<List<UserChatRolesResponse>> = chatService.checkFollowsChat(authHeader,chatIds)

}