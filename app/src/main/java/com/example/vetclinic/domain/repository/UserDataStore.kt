package com.example.vetclinic.domain.repository

interface UserDataStore {


    suspend fun saveUserSession(userId: String, accessToken: String, refreshToken: String)
    suspend fun saveUserId(userId: String)
    suspend fun saveAccessToken(accessToken: String)
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun saveUserRole(userRole: String)


    suspend fun getUserId(): String?
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserRole(): String?

    suspend fun clearUserSession()


}