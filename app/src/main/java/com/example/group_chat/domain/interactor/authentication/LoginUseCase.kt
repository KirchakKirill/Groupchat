package com.example.group_chat.domain.interactor.authentication

import com.example.group_chat.Utils.MapperResult
import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.model.LoginRequestModel
import com.example.group_chat.domain.model.LoginResponseModel
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val dataValidator: DataValidator,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
)
{
    suspend fun invoke(loginData:LoginRequestModel):Result<LoginResponseModel>
    {
        return when(val mappedData = Mapper.mapToLoginRequest(loginData)){

            is MapperResult.Error -> Result.failure(Exception(mappedData.message))
            is MapperResult.Success ->{
                return dataValidator.validate(
                    repositoryCall = {authRepository.login(mappedData.data!!)},
                    mapper = Mapper::mapToLoginResponseModel,
                    dispatcher = dispatcher
                )
            }
        }

    }
}