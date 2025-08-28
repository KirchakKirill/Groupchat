package com.example.group_chat.domain.interactor.chats

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.domain.repository.ChatRepository

class ChatFollowUseCase(
    private val chatRepository: ChatRepository
)
{
    suspend fun invoke(chatId:String,authHeader:String):Result<Unit>{
        val netRes = chatRepository.followChat(chatId,authHeader)
        if (netRes is NetworkResult.Error) return Result.failure(Exception(netRes.message))
        else
        {
            netRes.data?.let {
                return Result.success(it)
            } ?: return Result.failure(Exception("Received null Unit"))
        }
    }

}