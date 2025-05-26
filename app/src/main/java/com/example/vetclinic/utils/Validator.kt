package com.example.vetclinic.utils

interface Validator<T> {

    fun validate(input: T?): String?

}