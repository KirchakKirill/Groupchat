package com.example.group_chat.data.local.repository

import com.example.group_chat.Utils.toChatEntity
import com.example.group_chat.Utils.toChatModel
import com.example.group_chat.data.local.entity.ChatEntity
import com.example.group_chat.domain.model.ChatModel

interface LocalChatRepository
{
    suspend fun addChats(chats: List<ChatModel>)

    suspend fun getChats(limit:Int, offset:Int):List<ChatModel>

    suspend fun deleteChat(id:String)

    suspend fun existsChats(chatIds:List<String>):List<ChatModel>

    suspend fun addChat(chat:ChatModel)

    suspend fun getChatById(chatId:String):ChatModel
}