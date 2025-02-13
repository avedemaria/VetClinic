package com.example.vetclinic.domain

data class User(
    private val uid: String,
    private val userName: String,
    private val userLastName: String,
    private val petName: String,
    private val phoneNumber: String,
    private val email: String,

) {
}