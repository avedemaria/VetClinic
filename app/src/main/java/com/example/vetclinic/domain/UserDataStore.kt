package com.example.vetclinic.domain

interface UserDataStore {


    suspend fun saveUserSession(userId: String, accessToken: String)
    suspend fun saveUserId(userId: String)
    suspend fun saveAccessToken(token: String)
    suspend fun saveUserRole(userRole: String)

    suspend fun getUserId(): String?
    suspend fun getAccessToken(): String?
    suspend fun getUserRole(): String?

    suspend fun clearUserSession()


}