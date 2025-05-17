package com.example.vetclinic.utils

import com.example.vetclinic.domain.entities.user.UserInputData

interface Validator<T> {

    fun validate(input: T?): String?

}