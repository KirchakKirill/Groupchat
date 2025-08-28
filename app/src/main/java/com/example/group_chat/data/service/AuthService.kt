package com.example.group_chat.data.service

import com.example.group_chat.data.response.AuthResponse
import com.example.group_chat.data.response.LoginRequest
import com.example.group_chat.data.response.RegistryRequest
import com.example.group_chat.data.response.SuccessLoginResponse
import retrofit2.http.GET
import  retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService
{
    @GET("/verify-token")
    suspend fun  verify(@Query("token") token:String):Response<AuthResponse>

    @POST("/register")
    suspend fun registry(@Body body:RegistryRequest):Response<Unit>

    @POST("/login")
    suspend fun login(@Body body: LoginRequest):Response<SuccessLoginResponse>
}