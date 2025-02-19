package com.example.vetclinic.domain.authFeature

import com.example.vetclinic.domain.Repository
import jakarta.inject.Inject

class LogOutUseCase @Inject constructor(private val repository: Repository) {

    suspend fun logOut() {
        repository.logOut()
    }
}