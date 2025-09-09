package com.example.group_chat.data.repositoryImpl

import com.example.group_chat.Utils.BaseServerResponse
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.dataSource.ChatDataSource
import com.example.group_chat.data.remote.response.ChatResponse
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import com.example.group_chat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val chatDataSource: ChatDataSource
):BaseServerResponse(),ChatRepository
{
    override suspend fun createChatRoom(chatName:String,authHeader:String):NetworkResult<UserChatRolesResponse> = safeServerCall {
        chatDataSource.createChatRoom(chatName,authHeader)
    }
    override suspend fun followChat(chatId:String,authHeader:String):NetworkResult<UserChatRolesResponse> = safeServerCall {
        chatDataSource.followChat(chatId,authHeader)
    }

   override suspend fun getChatsAll(authHeader:String):NetworkResult<List<ChatResponse>> = safeServerCall {
        chatDataSource.getChatsAll(authHeader)
    }
   override suspend fun getChatsByUser(authHeader:String):NetworkResult<List<ChatResponse>>  = safeServerCall {
        chatDataSource.getChatsByUser(authHeader)
    }

    override suspend fun  getChatsPart(authHeader: String, offset:String, limit:String):NetworkResult<List<ChatResponse>> = safeServerCall {
        chatDataSource.getChatsPart(authHeader,offset,limit)
    }

    override suspend fun checkFollowsChat(
        authHeader: String,
        chatIds: List<String>
    ): NetworkResult<List<UserChatRolesResponse>>  = safeServerCall{
        chatDataSource.checkFollowsChat(authHeader,chatIds)
    }
}