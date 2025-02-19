package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.authFeature.User
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.CompletableDeferred


class RepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val userMapper: UserMapper,
    private val doctorMapper: DoctorMapper
) : Repository {


    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return try {
            var userSession: UserSession? = null

            Email.login(
                supabaseClient,
                onSuccess = { session ->
                    userSession = session
                }
            ) {
                this.email = email
                this.password = password
            }
            userSession?.let { Result.success(it) } ?: Result.failure(Exception("Session is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


//    override suspend fun registerUser(
//        email: String,
//        password: String
//    ): Result<UserSession> {
//        return try {
//            val userSession = suspendCoroutine { continuation ->
//                Email.signUp(
//                    supabaseClient,
//                    onSuccess = { session ->
//                        continuation.resume(session)
//                    }
//                ) {
//                    this.email = email
//                    this.password = password
//                }
//            }
//            Result.success(userSession)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.signUp(
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


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): io.github.jan.supabase.auth.user.UserInfo =
        supabaseClient.auth.currentUserOrNull() ?: throw Exception("No authenticated user found")


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {

        return try {

            val response = user
                .also {
                    Log.d(TAG, "Starting to add user: ${user.uid}")
                }
                .let { userMapper.userEntityToUserDto(it) }
                .also { Log.d(TAG, "Mapped to DTO: $it") }
                .let { supabaseApiService.addUser(it) }

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully added user to Supabase DB")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to add user. Error: $errorBody")
                Result.failure(Exception("Failed to add user: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while adding user", e)
            Result.failure(e)
        }
    }

    override suspend fun getDoctorList(): List<Doctor> {
        return try {
            supabaseApiService.getDoctors().let {
                doctorMapper.doctorDtoListToDoctorEntityList(it)
            }
        } catch (e: Exception) {
            Log.e("RepositoryImpl", "Error fetching doctors", e)
            emptyList()
        }
    }

    companion object {
        const val TAG = "RepositoryImpl"
    }
}









