package com.example.group_chat.domain.interactor.chats

import android.util.Log
import com.example.group_chat.Utils.FlowState
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.data.local.repository.LocalChatRepository
import com.example.group_chat.data.local.repository.LocalUserChatRepository
import com.example.group_chat.data.local.repository.LocalUserRepository
import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.UserChatRolesModel
import com.example.group_chat.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CheckFollowingChatsUseCase(
    private val chatRepository: ChatRepository,
    private val localUserChatRepository: LocalUserChatRepository,
    private val localUserRepository: LocalUserRepository,
    private val localChatRepository: LocalChatRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    operator fun invoke(authHeader:String,chatsIds:List<String>,userId:String): Flow<FlowState<List<UserChatRolesModel>>> = flow{
        var localForExc:List<UserChatRolesModel>? = null
        try {
            Log.d("CheckFollowingChats", "UserId: $userId")
            Log.d("CheckFollowingChats","chatIds: $chatsIds")
            val local = localUserChatRepository.getChatsFollowing(userId,chatsIds)
            Log.d("CheckFollowingChats","local: ${local}")
            localForExc = local
            if (local.isNotEmpty())
                emit(FlowState.Success(local))


            val localIds = local.map { it.chatId }
            var missingChats = chatsIds.filter { chatId ->
                !localIds.contains(chatId)
            }

            if (local.isEmpty()) {
                missingChats = chatsIds
            }

            Log.d("CheckFollowingChats","missingChatsId: ${missingChats}")

            if (missingChats.isEmpty()) return@flow

            val res = dataValidator.validateCollectionResponse({
                chatRepository.checkFollowsChat(
                    authHeader = authHeader,missingChats)
            }, Mapper::mapToUserChatRolesModel, dispatcher)

            Log.d("CheckFollowingChats","Res: ${res.getOrNull()}")
            when{
                 res.isSuccess -> {
                    val missingData = res.getOrNull() ?: emptyList()
                    Log.d("CheckFollowingChats","missingData: $missingData")
                    if (missingData.isNotEmpty()){
                        try {
                            Log.d("CheckFollowingChats", "Inserting ${missingData.size} records")
                            localUserChatRepository.followChats(missingData)
                            Log.d("CheckFollowingChats", "Inserting success")
                        }catch (e:Exception){
                            Log.e("CheckFollowingChats", "Insert failed: ${e.message}")
                            throw e
                        }

                    }
                    val newRes = localUserChatRepository.getChatsFollowing(userId,chatsIds)
                     Log.d("CheckFollowingChats","newRes: $newRes")
                    emit(FlowState.Success(newRes))
                }
                res.isFailure ->{
                    Log.e("CheckFollowingChats",res.exceptionOrNull()?.message?: "Unknown error")
                }
            }

        }
        catch (e:Exception){
            if (localForExc == null){
                Log.e("CheckFollowingChats",e.message ?: "Unknown error")
                emit(FlowState.Error(emptyList(),e))
            }
        }
    }.flowOn(Dispatchers.IO)
}

