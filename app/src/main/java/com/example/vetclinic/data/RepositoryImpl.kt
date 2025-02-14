package com.example.vetclinic.data

import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.Repository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession



class RepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService
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


    override suspend fun registerUser(
        email: String,
        password: String
    ): Result<UserSession> {
        return try {
            var userSession: UserSession? = null
            Email.signUp(
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


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): io.github.jan.supabase.auth.user.UserInfo =
        supabaseClient.auth.currentUserOrNull() ?: throw Exception("No authenticated user found")


    override suspend fun addUserToFirebaseDb(user: FirebaseUser) {
        val userReference = firebaseDb.getReference("Users")
        userReference.child(user.uid).setValue(user).await()
    }

}





