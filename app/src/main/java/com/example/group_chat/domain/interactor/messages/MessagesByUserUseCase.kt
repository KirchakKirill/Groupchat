package com.example.group_chat.domain.interactor.messages

import com.example.group_chat.Utils.Mapper
import com.example.group_chat.domain.model.MessageModel
import com.example.group_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class MessagesByUserUseCase (
    private val messageRepository: MessageRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    suspend fun  invoke(page:String,chatId:String,userId:String):Result<List<MessageModel>>
    {
        return  dataValidator.validateCollectionResponse(
            repositoryCall = {messageRepository.getAllMessageByUser(page,chatId,userId)},
            mapper = Mapper::mapMessageResponseToModel,
            dispatcher = dispatcher)
    }
}