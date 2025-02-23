package com.example.vetclinic.presentation.viewmodel

import io.github.jan.supabase.auth.user.UserSession

sealed class LoginState {

    data class Result(val userSession: UserSession) : LoginState()
    data class Error(val message: String?) : LoginState()
    object IsAuthenticated: LoginState()
    object LoggedOut: LoginState()
}