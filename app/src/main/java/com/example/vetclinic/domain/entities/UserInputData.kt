package com.example.vetclinic.domain.entities


data class UserInputData(
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
) {
    override fun toString(): String {
        return "UserInputData(name='$name', lastName='$lastName', phone='$phone', email='$email', password='***')"
    }
}

