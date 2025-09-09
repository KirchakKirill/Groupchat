package com.example.group_chat.domain.interactor.chats

import android.util.Log
import com.example.group_chat.Utils.FlowState
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.data.local.repository.LocalChatRepository
import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ChatsPartUseCase(
    private val chatRepository: ChatRepository,
    private val localChatRepository: LocalChatRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    operator fun invoke(authHeader:String,offset:String,limit:String): Flow<FlowState<List<ChatModel>>> = flow{
        val lim = limit.toIntOrNull() ?: run {
            emit(FlowState.Error(null,IllegalArgumentException("Invalid limit")))
            return@flow
        }
        val offs = offset.toIntOrNull() ?: run {
            emit(FlowState.Error(null,IllegalArgumentException("Invalid offset")))
            return@flow
        }
        emit(FlowState.Loading)
        var local:List<ChatModel>? =null
        try {
            local = withContext(dispatcher) {
                localChatRepository.getChats(lim, offs)
            }
            emit(FlowState.Success(local))
            if (local.size >= lim){
                return@flow
            }
            val res = dataValidator.validateCollectionResponse({
                chatRepository.getChatsPart(
                    authHeader = authHeader,
                    offset = (offs + local.size).toString(),
                    limit = (lim - local.size).toString()
                )
            }, Mapper::mapChatResponseToModel, dispatcher)
            when {
                res.isSuccess -> {
                    val remoteData = res.getOrNull() ?: emptyList()
                    localChatRepository.addChats(remoteData)
                    val updatedChats = localChatRepository.getChats(lim, offs)
                    emit(FlowState.Success(updatedChats))
                }
                res.isFailure -> {
                    Log.e("ChatFlow", "Failed take remote data chats")
                    emit(FlowState.Error(local,res.exceptionOrNull()?:Exception("Unknown exception")))
                }
            }
        }
        catch (e:Exception){
            Log.e("ChatFlow", "Error  remote data", e)
            if (local!=null)
                emit(FlowState.Error(local,e))
            else
                emit(FlowState.Error(emptyList(),e))
        }
    }.flowOn(Dispatchers.IO)

}

