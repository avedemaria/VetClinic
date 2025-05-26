package com.example.vetclinic.domain


interface LocalDataCleaner {
    suspend fun clearAllLocalData()
}
