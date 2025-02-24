package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.User
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getUserFromSupabase(): Result<UserInfo> {
        return repository.getCurrentUser()
    }

    suspend fun getUserFromRoom (userId: String): Result<User> {
        return repository.getCurrentUserFromRoom(userId)
    }
}