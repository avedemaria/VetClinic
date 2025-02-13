package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.google.firebase.auth.FirebaseUser
import jakarta.inject.Inject

class AddUserToFirebaseDb @Inject constructor (private val repository: Repository){

    suspend fun addUserToFirebaseDb (user: FirebaseUser) {
        repository.addUserToFirebaseDb(user)
    }
}