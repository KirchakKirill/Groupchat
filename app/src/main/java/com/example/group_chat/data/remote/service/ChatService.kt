package com.example.group_chat.data.remote.service

import com.example.group_chat.data.remote.response.ChatResponse
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService
{
        @POST("/create-chat")
        suspend fun createChatRoom(@Query("chatName") chatName:String,@Header("Authorization") authHeader:String):Response<UserChatRolesResponse>

        @POST("/follow_chat/{roomId}")
        suspend fun followChat(@Path("roomId") chatId:String,@Header("Authorization") authHeader:String):Response<UserChatRolesResponse>

        @GET("/chats")
        suspend fun getChatsAll(@Header("Authorization") authHeader:String):Response<List<ChatResponse>>

        @GET("/chats-part")
        suspend fun getChatsPart(@Header("Authorization") authHeader:String,
                                 @Query("offset") offset:String,
                                 @Query("limit") limit:String):Response<List<ChatResponse>>

        @GET("/chats-user")
        suspend fun getChatsByUser(@Header("Authorization") authHeader:String):Response<List<ChatResponse>>

        @POST("/check-chats")
        suspend fun checkFollowsChat(@Header("Authorization") authHeader:String, @Body chatIds:List<String>):Response<List<UserChatRolesResponse>>
}