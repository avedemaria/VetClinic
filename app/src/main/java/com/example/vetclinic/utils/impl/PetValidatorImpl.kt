package com.example.vetclinic.utils.impl

import com.example.vetclinic.domain.entities.pet.PetInputData
import com.example.vetclinic.utils.Validator
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PetValidatorImpl @Inject constructor() : Validator<PetInputData> {


    override fun validate(input: PetInputData?): String? {

        if (input == null) return "Данные пользователя не должны быть пустыми"

        if (input.name.isBlank() ||
            input.type.isBlank() ||
            input.gender.isBlank() ||
            input.bDay.isBlank()
        ) {
            return "Все поля питомца должны быть заполнены"
        }

        return null
    }
}