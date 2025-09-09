package com.example.group_chat.domain.interactor.validation

import android.util.Patterns

class ValidateEmail
{
    fun invoke(email:String):ValidationResult{
        if (email.isBlank()){
            return ValidationResult.Error("The email can`t be blank")
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return ValidationResult.Error("Not valid email")
        }

        return ValidationResult.Success()
    }

}