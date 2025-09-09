package com.example.group_chat.data.local.repository

import androidx.room.Insert
import androidx.room.Query
import com.example.group_chat.Utils.toChatEntity
import com.example.group_chat.Utils.toChatModel
import com.example.group_chat.data.local.dao.ChatDao
import com.example.group_chat.data.local.entity.ChatEntity
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.domain.model.MessageModel

class LocalChatRepositoryImpl(
    private val chatDao: ChatDao
):LocalChatRepository
{
    override suspend fun addChats(chats: List<ChatModel>) = chatDao.addChats(chats.map { it.toChatEntity()})

    override suspend fun getChats(limit:Int, offset:Int):List<ChatModel> =  chatDao.getChats(limit,offset).map { it.toChatModel()}

    override suspend fun deleteChat(id:String) = chatDao.deleteChat(id)

    override suspend fun existsChats(chatIds:List<String>):List<ChatModel> = chatDao.existsChats(chatIds).map { it.toChatModel() }

    override suspend fun getChatById(chatId: String): ChatModel {
        return chatDao.getChatById(chatId).toChatModel()
    }

    override suspend fun addChat(chat: ChatModel) {
        chatDao.addChat(chat = chat.toChatEntity())
    }


}