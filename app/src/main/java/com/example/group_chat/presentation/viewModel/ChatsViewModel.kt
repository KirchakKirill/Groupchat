package com.example.group_chat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.domain.interactor.chats.ChatAllUseCase
import com.example.group_chat.domain.interactor.chats.ChatByUserUseCase
import com.example.group_chat.domain.interactor.chats.ChatCreateUseCase
import com.example.group_chat.domain.interactor.chats.ChatFollowUseCase
import com.example.group_chat.domain.model.ChatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val chatAllUseCase: ChatAllUseCase,
    private val chatByUserUseCase: ChatByUserUseCase,
    private val chatCreateUseCase: ChatCreateUseCase,
    private val chatFollowUseCase: ChatFollowUseCase
):ViewModel()
{

    private val _chats = MutableStateFlow<List<ChatModel>>(emptyList())
    val chats:StateFlow<List<ChatModel>> = _chats.asStateFlow()

    private val _chatUser = MutableStateFlow<List<ChatModel>>(emptyList())
    val chatUser:StateFlow<List<ChatModel>>  = _chatUser.asStateFlow()

    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state:StateFlow<ChatState> = _state.asStateFlow()


     fun getAllChats(token:String){
        viewModelScope.launch {
            chatAllUseCase.invoke("Bearer $token")
                .onSuccess {
                    _chats.value = it
                    _state.value = ChatState.ChatSuccess
                }
                .onFailure { cause ->
                    _state.value = ChatState.ChatError(cause)
                }
        }
    }

    fun getChatByUser(token:String){
        viewModelScope.launch {
            chatByUserUseCase.invoke("Bearer $token")
                .onSuccess {
                    _chatUser.value = it
                    _state.value = ChatState.ChatSuccess
                }
                .onFailure { cause ->
                    _state.value = ChatState.ChatError(cause)
                }
        }
    }

    fun createChat(chatName:String,token:String){
        viewModelScope.launch {
            _state.value  = ChatState.Loading
            chatCreateUseCase.invoke(chatName,"Bearer $token")
                .onSuccess { chat ->
                    _chats.update { it + chat }
                    _chatUser.update { it + chat }
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
                _state.value = ChatState.Loading
                chatFollowUseCase.invoke(chatId,"Bearer $token")
                    .onSuccess {
                        _chatUser.update { it + exists }
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