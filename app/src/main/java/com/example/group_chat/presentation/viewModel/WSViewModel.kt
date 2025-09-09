package com.example.group_chat.presentation.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.Utils.FlowState
import com.example.group_chat.data.local.repository.LocalChatRepository
import com.example.group_chat.data.local.repository.LocalMessageRepository
import com.example.group_chat.data.remote.WebSocketConfig.WebSocketManager
import com.example.group_chat.data.remote.WebSocketConfig.WebSocketMessageType
import com.example.group_chat.domain.interactor.messages.MessagesByChatUseCase
import com.example.group_chat.domain.model.ContentReceive
import com.example.group_chat.domain.model.MessageModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class WSViewModel(
    private val webSocketManager: WebSocketManager,
    private val messagesByChatUseCase: MessagesByChatUseCase,
    private val localChatRepository: LocalChatRepository,
    private val localMessageRepository: LocalMessageRepository,
    private val gson:Gson
):ViewModel()
{
    private var _messages = MutableStateFlow<List<WebSocketMessageType>>(listOf())
    val messages:StateFlow<List<WebSocketMessageType>> = _messages.asStateFlow()

    private var _isConnected = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val isConnected:StateFlow<ConnectionState> = _isConnected.asStateFlow()

    private val _currentPortion = MutableStateFlow<Int>(0)
    val currentPortion:StateFlow<Int>  = _currentPortion.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _hasMore = mutableStateOf(true)
    val hasMore: State<Boolean> = _hasMore

    private val _currentCountMessageSession = mutableStateOf(0)
    val currentCountMessageSession:State<Int> =  _currentCountMessageSession

    private var currentAttempt = 0
    val LIMIT  = 10

    private var collectJob: Job? = null
    private var reconnectJob: Job? = null

    companion object{
        private const val SOCKET_URL = "ws://10.0.2.2:8080/ws_send"
        private const val RECONNECT_DELAY = 5000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val REPLAY_COUNT = 10
    }


    fun connectWebSocket(token:String,chatId:String,nameUser:String){
        collectJob?.cancel()
        if (isConnected.value == ConnectionState.Connected) return
        collectJob =  viewModelScope.launch {
            webSocketManager.openSocketConnection(SOCKET_URL,token,chatId,nameUser)
                .shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(RECONNECT_DELAY),
                    replay = REPLAY_COUNT
                )
                .onStart {
                    _isConnected.value = ConnectionState.Connecting
                    currentAttempt = 1
                    Log.d("WebSocket", "Connection state: Connecting")
                }
                .onEach { message ->
                    _messages.update { it + message }
                    _isConnected.value = ConnectionState.Connected
                    currentAttempt = 1
                    if (message is WebSocketMessageType.UserMessage) {
                        _currentCountMessageSession.value += 1
                        localMessageRepository.addMessage(message.data)
                    }

                    Log.d("WebSocket", "Connection state: Connected")
                }
                .catch { cause: Throwable? ->
                    if (cause!=null)
                    {
                        _isConnected.value = ConnectionState.Error(cause)
                        reconnect(token,chatId,nameUser)
                        Log.d("WebSocket", "Connection state: Error")
                    }
                    else{
                        _isConnected.value = ConnectionState.Disconnected
                        Log.d("WebSocket", "Connection state: Disconnected")
                    }
                }
                .launchIn(this)

        }
    }

    fun sendTextMessage(chatId: String, text: String, senderId: String,username:String) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val toSendMessage = gson.toJson( ContentReceive(
                    contentType = "text/plain",
                    content = text,
                    mediaContent = null
                ))
                webSocketManager.send(toSendMessage)
            }
            catch (e:Exception)
            {
                _isConnected.value = ConnectionState.Error(e)
            }
        }
    }
    fun sendMediaMessage(chatId: String,
                         content:String,
                         senderId: String,
                         mediaContent:String?,
                         mediaType:String,
                         username: String)
    {
        viewModelScope.launch {

            try {
                val toSendMessage = gson.toJson(
                    ContentReceive(
                        contentType = mediaType,
                        content = content,
                        mediaContent = mediaContent
                    )
                )
                webSocketManager.send(toSendMessage)
            }catch (e:Exception){
                _isConnected.value = ConnectionState.Error(e)
            }

        }
    }


    fun preloadMessages(chatId:String){
        Log.d("HASMORE","hasMore:${hasMore.value}, isLoading:${isLoading.value}")
        if (!hasMore.value || isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val followAt = localChatRepository.getChatById(chatId).createdAt
                val offset = currentCountMessageSession.value + (currentPortion.value * LIMIT)
                messagesByChatUseCase.invoke(chatId, followAt, LIMIT, offset)
                    .collect { messagesList ->

                        when (messagesList) {
                            is FlowState.Loading -> {
                                _isConnected.value = ConnectionState.Connecting
                            }

                            is FlowState.Success -> {

                                messagesList.data?.let {
                                    if  (it.isNotEmpty())
                                    {
                                        Log.d("PRELOAD","Data: $it")
                                        Log.d("PRELOAD","Reversed: ${it.reversed()}")
                                        Log.d("PRELOAD","Portion: ${currentPortion.value}")
                                        _messages.update {
                                              messagesList.data.reversed().map { msg ->
                                                        WebSocketMessageType.UserMessage(
                                                            msg
                                                        )
                                            } + it
                                        }
                                        Log.d("PRELOAD","MessagesOut: ${messages.value}")
                                        if  (it.size < LIMIT){
                                            _hasMore.value = false
                                        }
                                    }

                                }

                                _isConnected.value = ConnectionState.Connected
                                Log.d("HASMORE","hasMore(FlowState.Success): ${hasMore.value}")
                            }

                            is FlowState.Error -> {
                                _isConnected.value = ConnectionState.Error(messagesList.error)
                                _hasMore.value = true
                                Log.d("HASMORE","hasMore(FlowState.Error): ${hasMore.value}")
                            }
                        }

                    }
                _isLoading.value = false
                _currentPortion.value +=1
                Log.d("HASMORE","hasMore(end): ${hasMore.value}")
            }
            catch (e:Exception){
                _isConnected.value = ConnectionState.Error(e)
                _isLoading.value = false
                _hasMore.value = true
                Log.d("HASMORE","hasMore(catch): ${hasMore.value}")
            }
        }
    }



    private fun reconnect(token:String,chatId: String,nameUser: String){
        if (currentAttempt > MAX_RECONNECT_ATTEMPTS) {
            _isConnected.value = ConnectionState.Disconnected
            return
        }

        reconnectJob?.cancel()
        reconnectJob = viewModelScope.launch {
            delay(RECONNECT_DELAY * currentAttempt)
            currentAttempt+=1
            connectWebSocket(token,chatId,nameUser)
        }
    }

    override fun onCleared() {
        reconnectJob?.cancel()
        collectJob?.cancel()
        webSocketManager.close("ViewModel destroyed")
        _isConnected.value = ConnectionState.Disconnected
        super.onCleared()
    }

    sealed class ConnectionState{
        data object Connecting: ConnectionState()
        data object Disconnected : ConnectionState()
        data object Connected: ConnectionState()
        data class  Error(val error:Throwable?): ConnectionState()

    }
}