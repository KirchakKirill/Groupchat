package com.example.group_chat.data.dataSource

import com.example.group_chat.data.service.MessageService

class RemoteDataSource(private val messageService: MessageService)
{
    suspend fun getAllMessages(page:String) = messageService.getAllMessage(page)

    suspend fun  getAllMessageByType(page:String,
                                     chatId:String,
                                     typeContent:String) = messageService.getAllMessageByType(page,chatId,typeContent)


    suspend fun  getAllMessageByChat(page:String,
                                     chatId:String) = messageService.getAllMessageByChat(page,chatId)


    suspend fun  getAllMessageByUser(page:String,
                                     chatId:String,
                                     userId:String) = messageService.getAllMessageByUser(page,chatId,userId)
}