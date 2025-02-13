package com.example.vetclinic.domain

import com.google.firebase.auth.FirebaseUser

interface Repository {

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<FirebaseUser>

    fun logOut ()

    fun getCurrentUser(): FirebaseUser?


    suspend fun addUserToFirebaseDb (user: FirebaseUser)

}