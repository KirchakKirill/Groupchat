package com.example.group_chat.data.repositoryImpl

import com.example.group_chat.Utils.BaseServerResponse
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.dataSource.AuthRemoteDataSource
import com.example.group_chat.data.remote.response.AuthResponse
import com.example.group_chat.data.remote.response.LoginRequest
import com.example.group_chat.data.remote.response.RegistryRequest
import com.example.group_chat.data.remote.response.SuccessLoginResponse
import com.example.group_chat.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource
):BaseServerResponse(),AuthRepository
{
    override suspend fun verify(token:String):NetworkResult<AuthResponse> = safeServerCall {
        authRemoteDataSource.verify(token)
    }

    override suspend fun registry(registryData: RegistryRequest): NetworkResult<Unit> = safeServerCall {
        authRemoteDataSource.registry(registryData)
    }

    override suspend fun login(loginData: LoginRequest): NetworkResult<SuccessLoginResponse> = safeServerCall {
        authRemoteDataSource.login(loginData)
    }
}