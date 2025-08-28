package com.example.group_chat.domain.interactor.authentication

import com.example.group_chat.Utils.MapperResult
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.domain.model.RegisterModel
import com.example.group_chat.domain.repository.AuthRepository


class RegistryUseCase(
    private val authRepository: AuthRepository,
)
{
    suspend fun invoke(registryData: RegisterModel):Result<Unit>
    {
        when (val mappedData = Mapper.mapToRegistryRequest(registryData)){
            is MapperResult.Error -> return Result.failure(Exception(mappedData.message))
            is MapperResult.Success ->
            {
                val netRes = authRepository.registry(mappedData.data!!)
                if (netRes is NetworkResult.Error) return Result.failure(Exception(netRes.message))
                else
                {
                    netRes.data?.let {
                        return Result.success(it)
                    } ?: return Result.failure(Exception("Received null Unit"))
                }
            }
        }

    }
}