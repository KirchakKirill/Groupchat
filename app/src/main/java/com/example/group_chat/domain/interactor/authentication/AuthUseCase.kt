package com.example.group_chat.domain.interactor.authentication

import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.response.AuthResponse
import com.example.group_chat.domain.repository.AuthRepository

class AuthUseCase(
    private val authRepository: AuthRepository,
)
{
    suspend fun verify(token:String):Result<AuthResponse>
    {

        val netRes = authRepository.verify(token)
        if (netRes is NetworkResult.Error) return Result.failure(Exception("Server request failed"))
        netRes.data?.let {
            if (it.token==null) return Result.failure(Exception("Token cannot be null"))
            else return Result.success(it)
        } ?: return Result.failure(Exception("AuthResponse cannot be null"))
    }

}