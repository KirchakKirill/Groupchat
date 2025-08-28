package com.example.group_chat.data.repositoryImpl

import com.example.group_chat.Utils.BaseServerResponse
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.dataSource.RemoteDataSource
import com.example.group_chat.data.response.MessageResponse
import com.example.group_chat.domain.repository.MessageRepository


class MessageRepositoryImpl(
    private  val remoteDataSource: RemoteDataSource
):BaseServerResponse(),MessageRepository
{
    override suspend fun  getAllMessages(page:String):NetworkResult<List<MessageResponse>>
    {
        return  safeServerCall{remoteDataSource.getAllMessages(page)}
    }

    override suspend fun  getAllMessageByType(page: String, type:String, chatId:String):NetworkResult<List<MessageResponse>>
    {
        return safeServerCall { remoteDataSource.getAllMessageByType(page,chatId,type) }

    }

    override suspend fun  getAllMessageByChat(page: String, chatId:String):NetworkResult<List<MessageResponse>>
    {
        return safeServerCall { remoteDataSource.getAllMessageByChat(page,chatId) }
    }

    override suspend fun  getAllMessageByUser(page: String, chatId:String, userId:String):NetworkResult<List<MessageResponse>>
    {
        return safeServerCall { remoteDataSource.getAllMessageByUser(page,chatId,userId) }
    }

}