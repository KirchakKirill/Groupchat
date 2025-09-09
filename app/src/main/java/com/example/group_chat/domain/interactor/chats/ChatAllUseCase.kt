package com.example.group_chat.domain.interactor.chats

import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ChatAllUseCase(
    private val chatRepository: ChatRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    suspend fun invoke(authHeader:String):Result<List<ChatModel>>{
        return  dataValidator.validateCollectionResponse(
            repositoryCall = {chatRepository.getChatsAll(authHeader)},
            mapper = Mapper::mapChatResponseToModel,
            dispatcher = dispatcher
        )
    }

}