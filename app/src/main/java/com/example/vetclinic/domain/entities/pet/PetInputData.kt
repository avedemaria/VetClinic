package com.example.vetclinic.domain.entities.pet

data class PetInputData(
    val name: String = "",
    val type: String = "",
    val bDay: String = "",
    val gender: String = ""
) {
}