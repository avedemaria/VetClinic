package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import retrofit2.Response


const val TAG = "RepositoryUtils"

object RepositoryUtils {
    suspend fun <T, R> fetchData(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R,
        entityTag: String,
    ): Result<R> = kotlin.runCatching {
        val response = apiCall()
        val body = response.body()
            ?: throw Exception("Empty response body:${response.code()} - ${response.message()}")

        if (response.isSuccessful) {
            mapper(body)
        } else {
            throw Exception("Server's error: ${response.code()} - ${response.message()}")
        }
    }.onFailure { e ->
        Log.e(TAG, "Error fetching $entityTag {$e.message}")
    }


    suspend fun <T, R> addDataToSupabaseDb(
        entity: T,
        apiCall: suspend (R) -> Response<Unit>,
        mapper: (T) -> R,
    ): Result<Unit> = runCatching {
        val mappedEntity = mapper(entity)
        val response = apiCall(mappedEntity)

        if (response.isSuccessful) {
            Log.d(TAG, "Successfully added $entity to Supabase DB")
            Unit
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Failed to add $entity. Error: $errorBody")
            throw Exception("Failed to add $entity: ${response.code()} - $errorBody")
        }
    }.onFailure { e ->
        Log.e(TAG, "Error adding entity: ${e.message}")
    }

}