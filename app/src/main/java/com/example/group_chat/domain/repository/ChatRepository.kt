package com.example.group_chat.domain.repository

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.response.ChatResponse
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import com.example.group_chat.domain.model.UserChatRolesModel

interface ChatRepository
{
    suspend fun  getChatsAll(authHeader:String):NetworkResult<List<ChatResponse>>
    suspend fun  createChatRoom(chatName:String,authHeader:String):NetworkResult<UserChatRolesResponse>
    suspend fun followChat(chatId:String,authHeader:String):NetworkResult<UserChatRolesResponse>
    suspend fun getChatsByUser(authHeader:String):NetworkResult<List<ChatResponse>>
    suspend fun  getChatsPart(authHeader: String,offset:String,limit:String):NetworkResult<List<ChatResponse>>
    suspend fun checkFollowsChat( authHeader:String, chatIds:List<String>): NetworkResult<List<UserChatRolesResponse>>
}