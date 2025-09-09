package com.example.group_chat.domain.interactor.messages

import android.util.Log
import com.example.group_chat.Utils.FlowState
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.data.local.repository.LocalMessageRepository
import com.example.group_chat.data.local.repository.LocalUserRepository
import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.MessageModel
import com.example.group_chat.domain.model.UserModel
import com.example.group_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.Instant

class MessagesByChatUseCase(
    private val messageRepository: MessageRepository,
    private val dataValidator: DataValidator,
    private val localMessageRepository: LocalMessageRepository,
    private val localUserRepository: LocalUserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        chatId: String,
        followAt: String,
        limit: Int,
        offset: Int
    ): Flow<FlowState<List<MessageModel>>> = flow {
        try {
            emit(FlowState.Loading)
            val local = localMessageRepository.getMessagesWithTime(chatId, followAt, limit, offset)
            emit(FlowState.Success(local))
            Log.d("MessagesByChatTest", "local: ${local}")
            if (local.size>=limit){
                return@flow
            }

            Log.d("MessagesByChatTest","limit: ${limit-local.size}, offset: ${offset + local.size}")
            val remote = dataValidator.validateCollectionResponse(
                repositoryCall = {
                    messageRepository.getAllMessageByChat(
                        chatId,
                        followAt,
                        (limit - local.size).toString(),
                        (offset+local.size).toString()
                    )
                },
                mapper = Mapper::mapMessageResponseToModel,
                dispatcher = dispatcher
            )

            when {
                remote.isSuccess -> {
                    val  remoteMessages = remote.getOrNull() ?: emptyList()
                    Log.d("MessagesByChatTest", "remoteMessages: ${remoteMessages}")

                    val newUsers = remoteMessages.map { UserModel(id = it.senderId, username = it.username) }
                    localUserRepository.addUsers(newUsers)
                    if (remoteMessages.isNotEmpty()){
                        Log.d("MessagesByChatTest","Inserting ${remoteMessages.size} records")
                        localMessageRepository.addMessages(remoteMessages)
                        Log.d("MessagesByChatTest","Insert success")
                        val newRes = localMessageRepository.getMessagesWithTime(chatId,followAt,limit,offset)
                        Log.d("MessagesByChatTest","newRes: $newRes")
                        emit(FlowState.Success(newRes))
                    }

                }

                remote.isFailure ->{
                    val error = remote.exceptionOrNull()
                    Log.e("MessagesByChatTest",remote.exceptionOrNull()?.message ?:"Unknown exception")
                    emit(FlowState.Error(local,error))
                }
            }

        } catch (e: Exception) {
            Log.e("MessagesByChatTest", "${e.message} ")
            emit(FlowState.Error(emptyList(),e))
        }


    }.flowOn(Dispatchers.IO)
}