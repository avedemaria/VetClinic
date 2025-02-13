package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import jakarta.inject.Inject

class LogOutUseCase @Inject constructor(private val authRepository: Repository) {

    fun logOut() {
        authRepository.logOut()
    }
}