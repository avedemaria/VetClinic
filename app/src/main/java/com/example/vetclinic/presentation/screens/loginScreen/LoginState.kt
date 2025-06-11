package com.example.vetclinic.presentation.screens.loginScreen

import io.github.jan.supabase.auth.user.UserSession

sealed class LoginState {

    data class Result(val userSession: UserSession, val userRole: String) : LoginState()
    data object Error : LoginState()
    data object Loading : LoginState()
}