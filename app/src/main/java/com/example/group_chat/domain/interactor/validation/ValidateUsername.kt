package com.example.group_chat.domain.interactor.validation

class ValidateUsername {

    companion object{
        const val USERNAME_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$"
    }

    fun invoke(username:String):ValidationResult{
        if (username.length<5){
            return ValidationResult.Error("The username needs to consist of at least 5 characters")
        }

        if (!Regex(USERNAME_PATTERN).matches(username)){
            return ValidationResult.Error("Not valid username")
        }

        return ValidationResult.Success()
    }
}