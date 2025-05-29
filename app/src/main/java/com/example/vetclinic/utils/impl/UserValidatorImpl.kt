package com.example.vetclinic.utils.impl

import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.utils.FieldValidator
import com.example.vetclinic.utils.Validator
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class UserValidatorImpl @Inject constructor(private val fieldValidator: FieldValidator) :
    Validator<UserInputData> {

    override fun validate(input: UserInputData?): String? {

        if (input == null) return "Данные пользователя не должны быть пустыми"

        if (input.name.isBlank() ||
            input.lastName.isBlank() ||
            input.phone.isBlank() ||
            input.email.isBlank() ||
            input.password.isBlank()
        ) {
            return "Все поля пользователя должны быть заполнены"
        }

        fieldValidator.validatePhone(input.phone)?.let { return it }
        fieldValidator.validateEmail(input.email)?.let { return it }
        fieldValidator.validatePassword(input.password)?.let { return it }

        return null
    }


}