package com.example.group_chat.domain.interactor.validation

class ValidatePassword {

    fun invoke(password: String):ValidationResult{

        if (password.length < 7){
            return ValidationResult.Error("The password needs to consist of at least 7 characters")
        }

        val containsLetterAndDigits = password.any { it.isDigit() }
                && password.any{it.isLetter()}
        if (!containsLetterAndDigits)
            return ValidationResult.Error("The password contains at least on letter and digit")

        return ValidationResult.Success()
    }
}