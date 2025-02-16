package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.User
import io.github.jan.supabase.auth.user.UserInfo
import jakarta.inject.Inject

class AddUserToSupabaseDb @Inject constructor(private val repository: Repository) {

    suspend fun addUserToSupabaseDb(user: User) {
        repository.addUserToSupabaseDb(user)
    }
}