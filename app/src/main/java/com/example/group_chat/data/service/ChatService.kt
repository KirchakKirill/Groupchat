package com.example.group_chat.data.service

import com.example.group_chat.data.response.ChatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService
{
        @POST("/create-chat")
        suspend fun createChatRoom(@Query("chatName") chatName:String,@Header("Authorization") authHeader:String):Response<ChatResponse>

        @POST("/follow_chat/{roomId}")
        suspend fun followChat(@Path("roomId") chatId:String,@Header("Authorization") authHeader:String):Response<Unit>

        @GET("/chats")
        suspend fun getChatsAll(@Header("Authorization") authHeader:String):Response<List<ChatResponse>>

        @GET("/chats-user")
        suspend fun getChatsByUser(@Header("Authorization") authHeader:String):Response<List<ChatResponse>>
}