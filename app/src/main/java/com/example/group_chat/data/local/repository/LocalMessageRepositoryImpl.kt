package com.example.group_chat.data.local.repository

import com.example.group_chat.Utils.toMessageEntity
import com.example.group_chat.Utils.toMessageModel
import com.example.group_chat.data.local.dao.MessageDAO
import com.example.group_chat.data.local.entity.MessageEntity
import com.example.group_chat.domain.model.MessageModel
import org.threeten.bp.Instant


class LocalMessageRepositoryImpl(
    private val messageDAO: MessageDAO
):LocalMessageRepository
{
    override suspend fun  addMessage(messageModel: MessageModel) {
        messageDAO.addMessage(messageModel.toMessageEntity())
    }

     override suspend fun getMessages(chatId:String, limit:Int, offset: Int):List<MessageModel> {
       return  messageDAO.getMessages(chatId,limit,offset).map { it.toMessageModel()}
    }

    override suspend fun deleteMessage(id:String) = messageDAO.deleteMessage(id)

    override suspend fun getMessagesWithTime(chatId:String, followTime: String,limit: Int,offset:Int):List<MessageModel>{
        return messageDAO.getMessagesWithTime(chatId,limit,followTime,offset).map { it.toMessageModel() }
    }

    override suspend fun addMessages(msg:List<MessageModel>){
        return messageDAO.addMessages(msg.map { it.toMessageEntity() })
    }
}