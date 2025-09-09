package com.example.group_chat.domain.interactor.validation

sealed class ValidationResult(
    val errorMessage:String? = null
) {
    class Success:ValidationResult()
    class Error(errorMessage: String?):ValidationResult(errorMessage)
}