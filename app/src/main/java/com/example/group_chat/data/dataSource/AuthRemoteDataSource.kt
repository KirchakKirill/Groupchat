package com.example.group_chat.data.dataSource

import com.example.group_chat.data.service.AuthService
import com.example.group_chat.data.response.AuthResponse
import com.example.group_chat.data.response.LoginRequest
import com.example.group_chat.data.response.RegistryRequest
import com.example.group_chat.data.response.SuccessLoginResponse
import retrofit2.Response

class AuthRemoteDataSource(private val authService: AuthService)
{
    suspend fun verify(token:String):Response<AuthResponse> = authService.verify(token)
    suspend fun registry(registryData:RegistryRequest):Response<Unit> = authService.registry(registryData)
    suspend fun login(loginData:LoginRequest):Response<SuccessLoginResponse> = authService.login(loginData)

}