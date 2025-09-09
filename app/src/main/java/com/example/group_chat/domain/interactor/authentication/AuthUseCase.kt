package com.example.group_chat.domain.interactor.authentication

import com.example.group_chat.Utils.Mapper
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.response.AuthResponse
import com.example.group_chat.domain.model.LoginGoogleModel
import com.example.group_chat.domain.repository.AuthRepository

class AuthUseCase(
    private val authRepository: AuthRepository,
)
{
    suspend fun verify(token:String):Result<LoginGoogleModel>
    {
        val netRes = authRepository.verify(token)
        if (netRes is NetworkResult.Error) return Result.failure(Exception("Server request failed"))
        netRes.data?.let {
             val mapped = Mapper.mapToLoginGoogleModel(it)
             val res = mapped.data?.let { model ->
                return Result.success(model)
            } ?: Result.failure<LoginGoogleModel>(Exception(mapped.message))
            return  res
        } ?: return Result.failure(Exception("AuthResponse cannot be null"))
    }

}