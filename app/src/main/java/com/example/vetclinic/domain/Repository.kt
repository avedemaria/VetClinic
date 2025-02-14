package com.example.vetclinic.domain

import com.google.firebase.auth.FirebaseUser
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession

interface Repository {

    suspend fun loginUser(email: String, password: String): Result<UserSession>

    suspend fun registerUser(
        email: String,
        password: String,
    ): Result<UserSession>

    suspend fun logOut ()

    fun getCurrentUser(): UserInfo


    suspend fun addUserToFirebaseDb (user: FirebaseUser)

}