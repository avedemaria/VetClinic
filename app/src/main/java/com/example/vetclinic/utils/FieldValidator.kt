package com.example.vetclinic.utils

interface FieldValidator {
    fun validatePhone(phone: String): String?
    fun validateEmail(email: String): String?
    fun validatePassword(password: String): String?
}