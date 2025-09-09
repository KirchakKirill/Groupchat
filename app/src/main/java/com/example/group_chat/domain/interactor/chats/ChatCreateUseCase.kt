package com.example.group_chat.domain.interactor.chats

import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.domain.model.UserChatRolesModel
import com.example.group_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ChatCreateUseCase(
    private val chatRepository: ChatRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    suspend fun invoke(chatName:String,authHeader:String):Result<UserChatRolesModel>{
        return  dataValidator.validate(
            repositoryCall = {chatRepository.createChatRoom(chatName,authHeader)},
            mapper = Mapper::mapToUserChatRolesModel,
            dispatcher = dispatcher
        )
    }
}