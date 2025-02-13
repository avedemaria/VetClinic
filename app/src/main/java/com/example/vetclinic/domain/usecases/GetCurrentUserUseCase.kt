package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.google.firebase.auth.FirebaseUser
import jakarta.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authRepository: Repository) {

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}