package com.example.vetclinic.domain

import kotlinx.coroutines.flow.Flow

interface UserDataStore {


    val userIdFlow: Flow<String?>

    suspend fun saveUserSession(userId: String, accessToken: String)
    suspend fun saveUserId(userId: String)
    suspend fun saveAccessToken(token: String)

    suspend fun getUserId(): String?
    suspend fun getAccessToken(): String?

    suspend fun clearUserSession()


}