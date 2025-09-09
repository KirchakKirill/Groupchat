package com.example.group_chat.presentation.viewModel


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.group_chat.Utils.EncryptionHelper
import com.example.group_chat.domain.interactor.authentication.RegistryUseCase
import com.example.group_chat.domain.interactor.validation.ValidateEmail
import com.example.group_chat.domain.interactor.validation.ValidatePassword
import com.example.group_chat.domain.interactor.validation.ValidateUsername
import com.example.group_chat.domain.interactor.validation.ValidationResult
import com.example.group_chat.domain.model.RegisterModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registryUseCase: RegistryUseCase,
    private val encryptionHelper: EncryptionHelper,
    private val validateEmail: ValidateEmail,
    private val validateUsername: ValidateUsername,
    private val validatePassword: ValidatePassword
) :ViewModel()
{
    private val _registerInfo = MutableStateFlow(RegisterModel())
    val registerInfo: StateFlow<RegisterModel> = _registerInfo.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.RegisterNothing)
    val registerState:StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _validationErrors = MutableStateFlow<List<ValidationError>>(emptyList())
    val validationErrors: StateFlow<List<ValidationError>> = _validationErrors.asStateFlow()


    fun register()
    {
        viewModelScope.launch {
            _validationErrors.update { emptyList() }
            val (email,username,password) = listOf(registerInfo.value.email,registerInfo.value.username,registerInfo.value.password)
            val emailValidateRes = validateEmail.invoke(email)
            val usernameValidateRes = validateUsername.invoke(username)
            val passwordValidateRes = validatePassword.invoke(password)

            if (emailValidateRes is ValidationResult.Error)
                _validationErrors.update { it + ValidationError.EmailValidationError(emailValidateRes.errorMessage) }

            if (usernameValidateRes is ValidationResult.Error)
                _validationErrors.update { it + ValidationError.UsernameValidationError(usernameValidateRes.errorMessage) }

            if (passwordValidateRes is ValidationResult.Error)
                _validationErrors.update { it + ValidationError.PasswordValidationError(passwordValidateRes.errorMessage) }

            if (validationErrors.value.isNotEmpty()) return@launch

            _registerInfo.update { it.copy(password = encryptPassword(it.password))}
            registryUseCase.invoke(registerInfo.value)
                .onSuccess {
                    _registerState.value = RegisterState.RegisterSuccess
                }
                .onFailure {
                    _registerState.value = RegisterState.RegisterError(it)
                }
        }
    }

    private fun encryptPassword( password: String):String{
        return encryptionHelper.encrypt(password)
    }


    fun updateEmail(email:String){
        _registerInfo.update { it.copy(email = email) }
    }

    fun updateUsername(username:String){
        _registerInfo.update { it.copy(username = username) }
    }

    fun updatePassword(password: String){
        _registerInfo.update { it.copy(password = password) }
    }

    fun updateFirstName(name: String){
        _registerInfo.update { it.copy(firstName = name) }
    }

    fun updateSecondName(name: String){
        _registerInfo.update { it.copy(secondName = name) }
    }

    fun updateAvatar(avatar: String){
        _registerInfo.update { it.copy(avatar = avatar) }
    }

    fun resetState(){
        _registerState.value = RegisterState.RegisterNothing
    }
    sealed class RegisterState{
        data class RegisterError(val e:Throwable?):RegisterState()
        data object RegisterSuccess:RegisterState()
        data object RegisterNothing:RegisterState()
    }

    sealed class ValidationError(
        val message:String?=null
    ){
        class EmailValidationError(message: String?):ValidationError(message)
        class UsernameValidationError(message: String?):ValidationError(message)
        class PasswordValidationError(message: String?):ValidationError(message)
    }
}