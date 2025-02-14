package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.google.firebase.auth.FirebaseUser
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val repository: Repository) {

    fun getCurrentUser(): UserInfo {
        return repository.getCurrentUser()
    }
}