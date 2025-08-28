package com.example.group_chat.domain.repository

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.response.AuthResponse
import com.example.group_chat.data.response.LoginRequest
import com.example.group_chat.data.response.RegistryRequest
import com.example.group_chat.data.response.SuccessLoginResponse

interface AuthRepository
{
    suspend fun verify(token:String):NetworkResult<AuthResponse>
    suspend fun registry(registryData: RegistryRequest): NetworkResult<Unit>
    suspend fun login(loginData: LoginRequest): NetworkResult<SuccessLoginResponse>
}