package com.example.group_chat.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.domain.interactor.messages.MessagesAllUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByChatUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByTypeUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByUserUseCase
import com.example.group_chat.domain.model.MessageModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MainViewModel(
    private val messagesAllUseCase: MessagesAllUseCase,
    private val messagesByChatUseCase: MessagesByChatUseCase,
    private val messagesByTypeUseCase: MessagesByTypeUseCase,
    private val messagesByUserUseCase: MessagesByUserUseCase
):ViewModel()
{
    companion object {
        const val DEFAULT_PAGE = "0"
    }

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean>
        get() = _isLoading

    private val _allMessages = MutableLiveData<Result<List<MessageModel>>>()
    val allMessages:LiveData<Result<List<MessageModel>>>
        get() = _allMessages

    private val _allMessagesByType = MutableLiveData<Result<List<MessageModel>>>()
    val allMessagesByType:LiveData<Result<List<MessageModel>>>
        get() = _allMessagesByType

    private val _allMessagesByChat = MutableLiveData<Result<List<MessageModel>>>()
    val allMessagesByChat:LiveData<Result<List<MessageModel>>>
        get() = _allMessagesByChat

    private val _allMessagesByUser = MutableLiveData<Result<List<MessageModel>>>()
    val allMessagesByUser:LiveData<Result<List<MessageModel>>>
        get() = _allMessagesByUser

    private var currentJob: Job? = null

    init {
        getAllMessages(DEFAULT_PAGE)
    }

     fun getAllMessages(page:String){
         currentJob?.cancel()
         currentJob = viewModelScope.launch {
             _isLoading.value = true
             _allMessages.value =  messagesAllUseCase.invoke(page)
             _isLoading.value = false
        }
    }

    fun getMessagesByType(page:String,type:String,chatId:String)
    {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _isLoading.value = true
            _allMessagesByType.value = messagesByTypeUseCase.invoke(page,type,chatId)
            _isLoading.value = false
        }
    }

    fun getMessagesByChat(page:String,chatId:String){
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _isLoading.value = true
            _allMessagesByChat.value = messagesByChatUseCase.invoke(page,chatId)
            _isLoading.value = false
        }
    }

    fun getMessagesByUser(page:String,chatId:String,userId:String){
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _isLoading.value = true
            _allMessagesByUser.value = messagesByUserUseCase.invoke(page,chatId,userId)
            _isLoading.value = false
        }
    }
}