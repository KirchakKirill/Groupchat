package com.example.group_chat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.Utils.FlowState
import com.example.group_chat.data.local.repository.LocalChatRepository
import com.example.group_chat.data.local.repository.LocalUserChatRepository
import com.example.group_chat.domain.interactor.chats.ChatCreateUseCase
import com.example.group_chat.domain.interactor.chats.ChatFollowUseCase
import com.example.group_chat.domain.interactor.chats.ChatsPartUseCase
import com.example.group_chat.domain.interactor.chats.CheckFollowingChatsUseCase
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.domain.model.UserChatRolesModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val chatCreateUseCase: ChatCreateUseCase,
    private val chatFollowUseCase: ChatFollowUseCase,
    private val chatPartUseCase: ChatsPartUseCase,
    private val checkFollowingChatsUseCase: CheckFollowingChatsUseCase,
    private val localChatRepository: LocalChatRepository,
    private  val localUserChatRepository: LocalUserChatRepository
):ViewModel()
{

    private val _chats = MutableStateFlow<List<ChatModel>>(emptyList())
    val chats:StateFlow<List<ChatModel>> = _chats.asStateFlow()

    private val _chatUser = MutableStateFlow<List<UserChatRolesModel>>(emptyList())
    val chatUser:StateFlow<List<UserChatRolesModel>>  = _chatUser.asStateFlow()

    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state:StateFlow<ChatState> = _state.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage:StateFlow<Int> = _currentPage.asStateFlow()

    val LIMIT = 5;

    fun loadStartInfo(token:String,offset: Int,userId: String){
        getAllChats(token,offset,userId)
    }

     private fun getAllChats(token:String,offset: Int,userId:String){
        viewModelScope.launch {
            chatPartUseCase.invoke("Bearer $token",offset.toString(),LIMIT.toString())
                .collect { chatsRes ->
                    when (chatsRes) {
                        is FlowState.Loading -> {
                            _state.value = ChatState.Loading
                        }

                        is FlowState.Success -> {
                            _chats.value = chatsRes.data!!
                            if(chats.value.isNotEmpty()){
                                getChatByUser(token, userId, chatsRes.data)
                            }else{
                                _state.value = ChatState.ChatSuccess
                            }
                        }

                        is FlowState.Error -> {
                            _chats.value = chatsRes.data ?: emptyList()
                            _state.value = ChatState.ChatSuccess
                        }
                    }
                }


        }
    }




    private fun getChatByUser(token:String,userId:String,chats:List<ChatModel>){
        viewModelScope.launch {
                if (chats.isNotEmpty()) {
                    val chatIds = chats.map { it.id }
                    checkFollowingChatsUseCase.invoke("Bearer $token", chatIds, userId)
                        .collect { flowState ->
                            when {
                                flowState is FlowState.Success -> {
                                    _chatUser.value = flowState.data ?: emptyList()
                                    _state.value = ChatState.ChatSuccess
                                }

                                flowState is FlowState.Error -> {
                                    _state.value = ChatState.ChatError(flowState.error)
                                }
                            }
                        }
                }

        }
    }

    fun getNextPage(token:String,userId:String){
        _currentPage.value += 1
        val offset = _currentPage.value * LIMIT
        getAllChats(token,offset, userId )
    }
    fun getPrevPage(token:String,userId:String){
        _currentPage.value -= 1
        val offset = _currentPage.value * LIMIT
        getAllChats(token,offset, userId)
    }

    fun createChat(chatName:String,token:String){
        viewModelScope.launch {
            _state.value  = ChatState.Loading
            chatCreateUseCase.invoke(chatName,"Bearer $token")
                .onSuccess { chat ->
                    val chatModel =  ChatModel(
                        id = chat.chatId,
                        name = chatName,
                        createdAt = chat.joinedAt
                    )
                    if (chats.value.size < LIMIT)
                    {
                        _chats.update { it + chatModel  }
                    }
                    localChatRepository.addChat(chatModel)
                    localUserChatRepository.followChat(chat)
                    _chatUser.update { it + chat}
                    _state.value = ChatState.ChatSuccess
                }
                .onFailure {
                    _state.value = ChatState.ChatError(it)
                }
        }
    }

    fun followChat(chatId:String,token:String){
        viewModelScope.launch {
            val exists = chats.value.firstOrNull{it.id==chatId}
            if (exists==null)
            {
                _state.value = ChatState.ChatError(Exception("Incorrect chat id"))
                return@launch
            }
            else{
                chatFollowUseCase.invoke(chatId,"Bearer $token")
                    .onSuccess { fChat->
                        _chatUser.update { it + fChat }
                        localUserChatRepository.followChat(fChat)
                        _state.value = ChatState.ChatSuccess
                    }
                    .onFailure {
                        _state.value = ChatState.ChatError(it)
                    }
            }

        }
    }

    sealed class ChatState
    {
        data object Loading:ChatState()
        data object ChatSuccess: ChatState()
        data class ChatError(val e:Throwable?):ChatState()
    }
}