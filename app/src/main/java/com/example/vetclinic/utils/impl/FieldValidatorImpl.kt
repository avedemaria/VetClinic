package com.example.vetclinic.utils.impl

import android.util.Patterns
import com.example.vetclinic.utils.FieldValidator
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FieldValidatorImpl @Inject constructor(): FieldValidator {

    private val phoneRegex = "^(?:\\+7|7|8)(\\d{10})$".toRegex()

    override fun validatePhone(phone: String): String? {
        return if (!phoneRegex.matches(phone)) "Введите корректный номер телефона" else null
    }

    override fun validateEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            "Введите корректный email" else null
    }

    override fun validatePassword(password: String): String? {
        return if (password.length < 6) "Пароль должен быть не менее 6 символов" else null
    }
}