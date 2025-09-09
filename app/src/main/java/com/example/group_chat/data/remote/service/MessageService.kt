package com.example.group_chat.data.remote.service

import com.example.group_chat.data.remote.response.MessageResponse
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

    @GET("/messages-chat")
    suspend fun  getAllMessageByChat(@Query("chatId") chatId:String,
                                     @Query("followAt") followAt:String,
                                     @Query("limit") limit:String,
                                     @Query("offset") offset:String): Response<List<MessageResponse>>

    @GET("/messages-user/{page}")
    suspend fun  getAllMessageByUser(@Path("page") page:String,
                                   @Query("chatId") chatId:String,
                                   @Query("userId") userId:String): Response<List<MessageResponse>>


}