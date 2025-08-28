package com.example.group_chat.data.service

import com.example.group_chat.data.response.MessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageService
{
    @GET("/messages/{page}")
    suspend fun  getAllMessage(@Path("page") page:String ): Response<List<MessageResponse>>

    @GET("/messages-type/{page}")
    suspend fun  getAllMessageByType(@Path("page") page:String,
                                   @Query("chatId") chatId:String,
                                   @Query("type") typeContent:String): Response<List<MessageResponse>>

    @GET("/messages-chat/{page}")
    suspend fun  getAllMessageByChat(@Path("page") page:String,
                                   @Query("chatId") chatId:String): Response<List<MessageResponse>>

    @GET("/messages-user/{page}")
    suspend fun  getAllMessageByUser(@Path("page") page:String,
                                   @Query("chatId") chatId:String,
                                   @Query("userId") userId:String): Response<List<MessageResponse>>


}