package com.example.group_chat.data.repositoryImpl

import com.example.group_chat.Utils.BaseServerResponse
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.dataSource.RemoteDataSource
import com.example.group_chat.data.remote.response.MessageResponse
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

    override suspend fun  getAllMessageByChat(chatId:String,followAt:String,limit:String,offset:String):NetworkResult<List<MessageResponse>>
    {
        return safeServerCall { remoteDataSource.getAllMessageByChat(chatId,followAt,limit,offset) }
    }

    override suspend fun  getAllMessageByUser(page: String, chatId:String, userId:String):NetworkResult<List<MessageResponse>>
    {
        return safeServerCall { remoteDataSource.getAllMessageByUser(page,chatId,userId) }
    }

}