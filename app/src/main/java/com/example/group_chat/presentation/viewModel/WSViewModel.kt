package com.example.group_chat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.data.WebSocketConfig.WebSocketManager
import com.example.group_chat.data.WebSocketConfig.WebSocketMessageType
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
    private val gson:Gson
):ViewModel()
{
    private var _messages = MutableStateFlow<List<WebSocketMessageType>>(listOf())
    val messages:StateFlow<List<WebSocketMessageType>> = _messages.asStateFlow()

    private var _isConnected = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val isConnected:StateFlow<ConnectionState> = _isConnected.asStateFlow()

    private var currentAttempt = 0

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
                }
                .onEach { message ->
                    _messages.update { it + message }
                    _isConnected.value = ConnectionState.Connected
                    currentAttempt = 1
                }
                .catch { cause: Throwable? ->
                    if (cause!=null)
                    {
                        _isConnected.value = ConnectionState.Error(cause)
                        reconnect(token,chatId,nameUser)
                    }
                    else{
                        _isConnected.value = ConnectionState.Disconnected
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
                val myMessage = MessageModel(
                    chatId = chatId,
                    senderId = senderId,
                    contentType = "text/plain",
                    content = text,
                    username = username,
                    mediaContent = null
                )
                _messages.update { it + WebSocketMessageType.UserMessage(myMessage) }
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
                val messageModel = MessageModel(
                    chatId = chatId,
                    senderId = senderId,
                    contentType = mediaType,
                    content = content,
                    mediaContent = mediaContent,
                    username = username
                )
                _messages.update { it + WebSocketMessageType.UserMessage(messageModel) }
            }catch (e:Exception){
                _isConnected.value = ConnectionState.Error(e)
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