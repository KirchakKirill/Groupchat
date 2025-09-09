package com.example.group_chat.data.local.repository

import com.example.group_chat.domain.model.MessageModel

interface LocalMessageRepository
{
    suspend fun  addMessage(messageModel: MessageModel)

    suspend fun getMessages(chatId:String, limit:Int, offset: Int):List<MessageModel>

    suspend fun deleteMessage(id:String)

    suspend fun getMessagesWithTime(chatId:String,followTime: String,limit: Int,  offset:Int):List<MessageModel>

    suspend fun addMessages(msg:List<MessageModel>)
}