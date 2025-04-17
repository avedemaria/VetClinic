package com.example.vetclinic.presentation.loginScreen

import io.github.jan.supabase.auth.user.UserSession

sealed class LoginState {

    data class Result(val userSession: UserSession, val userRole: String) : LoginState()
    data class Error(val message: String?) : LoginState()

    //    object IsAuthenticated: LoginState()
//    object LoggedOut: LoginState()
    object Loading : LoginState()
}