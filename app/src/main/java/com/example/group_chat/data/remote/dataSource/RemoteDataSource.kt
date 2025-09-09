package com.example.group_chat.data.remote.dataSource

import com.example.group_chat.data.remote.service.MessageService

class RemoteDataSource(private val messageService: MessageService)
{
    suspend fun getAllMessages(page:String) = messageService.getAllMessage(page)

    suspend fun  getAllMessageByType(page:String,
                                     chatId:String,
                                     typeContent:String) = messageService.getAllMessageByType(page,chatId,typeContent)


    suspend fun  getAllMessageByChat(chatId:String, followAt:String,limit:String,offset:String) = messageService.getAllMessageByChat(chatId,followAt,limit,offset)


    suspend fun  getAllMessageByUser(page:String,
                                     chatId:String,
                                     userId:String) = messageService.getAllMessageByUser(page,chatId,userId)
}