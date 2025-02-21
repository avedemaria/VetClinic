package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.CodeReview
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.authFeature.User
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
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
    private val doctorMapper: DoctorMapper
) : Repository {


    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
//        return try {
//
//            var userSession: UserSession? = null
//
//            Email.login(
//                supabaseClient,
//                onSuccess = { session ->
//                    userSession = session
//                }
//            ) {
//                this.email = email
//                this.password = password
//            }
        @CodeReview("Всегда будет null? Сейчас onSuccess вызывается позже")
//            userSession?.let { Result.success(it) } ?: Result.failure(Exception("Session is null"))
//        } catch (e: Exception) {
//            Result.failure(e)
//        }

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
                    Log.d("ANYA", "registerUser onSuccess")
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }

            // Result.success(deferred.await())
            @CodeReview("Добавляем таймаут на случай, если onSuccess не вызовется")
            Result.success(withTimeout(10_000) { deferred.await() })
        } catch (e: Exception) {
            Log.d("ANYA", "Result.failure(e)")

            Result.failure(e)
        }
    }


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    @CodeReview("Тут исключение бросается. Лучше возвращать Result или написать аннотацию с исключением")
    override fun getCurrentUser(): io.github.jan.supabase.auth.user.UserInfo =
        supabaseClient.auth.currentUserOrNull() ?: throw Exception("No authenticated user found")


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> {
        @CodeReview("Можно без try-catch")
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

    override suspend fun getDoctorList(): List<Doctor> {
        return try {
            @CodeReview("Нет проверки на failure. Сервер может отдать 500, 404 и т.п.")
            supabaseApiService.getDoctors().let {
                doctorMapper.doctorDtoListToDoctorEntityList(it)
            }
        } catch (e: Exception) {
            Log.e("RepositoryImpl", "Error fetching doctors", e)
            emptyList()
        }
    }

    override suspend fun checkUserSession(): Boolean {
        return try {
            return supabaseClient.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val TAG = "RepositoryImpl"
    }
}









