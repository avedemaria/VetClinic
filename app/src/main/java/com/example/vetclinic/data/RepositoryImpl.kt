package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.database.model.UserDbModel
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout


class RepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val userMapper: UserMapper,
    private val petMapper: PetMapper,
    private val doctorMapper: DoctorMapper
) : Repository {


    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.login(
                supabaseClient,
                onSuccess = { session ->
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }
            Result.success(deferred.await())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.signUp(
                supabaseClient,
                onSuccess = { session ->
                    Log.d(TAG, "registerUser onSuccess")
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }

            Result.success(withTimeout(10_000) { deferred.await() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): Result<UserInfo> {
        return supabaseClient.auth.currentUserOrNull()?.let { Result.success(it) }
            ?: Result.failure(Exception("No user found"))
    }


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {

        return kotlin.runCatching {

            val response = supabaseApiService.addUser(userMapper.userEntityToUserDto(user))

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully added user to Supabase DB")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to add user. Error: $errorBody")
                Result.failure(Exception("Failed to add user: ${response.code()} - $errorBody"))
            }
        }
    }


    override suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> {

        return kotlin.runCatching {

            val response = supabaseApiService.addPet(petMapper.petEntityToPetDto(pet))

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully added pet to Supabase DB")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to add pet. Error: $errorBody")
                Result.failure(Exception("Failed to add pet: ${response.code()} - $errorBody"))
            }
        }
    }

    override suspend fun getDoctorList(): Result<List<Doctor>> = runCatching {
        val response = supabaseApiService.getDoctors()

        val body = response.body()
            ?: throw Exception("Empty response body: ${response.code()} - ${response.message()}")
        if (response.isSuccessful) {
            doctorMapper.doctorDtoListToDoctorEntityList(body)
        } else {
            throw Exception("Server's error: ${response.code()} - ${response.message()}")
        }
    }.onFailure { e ->
        Log.e("RepositoryImpl", "Error fetching doctors ${e.message}")
    }


    override suspend fun checkUserSession(): Boolean {
        return try {
            return supabaseClient.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun addUserToRoom(user: User, pet: Pet) {
        vetClinicDao.insertUser(userMapper.userEntityToUserDbModel(user))
        vetClinicDao.insertPet(petMapper.petEntityToPetDbModel(pet))
    }

    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> =
        runCatching {
            val userDbModel = vetClinicDao.getUserById(userId)
            userMapper.userDbModelToUserEntity(userDbModel)
        }.onFailure {
            Log.e(TAG, "Error while getting user from Room", it)
        }

    companion object {
        const val TAG = "RepositoryImpl"
    }
}









