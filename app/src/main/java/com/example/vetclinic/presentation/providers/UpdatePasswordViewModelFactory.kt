//package com.example.vetclinic.presentation.providers
//
//import android.os.Bundle
//import androidx.lifecycle.AbstractSavedStateViewModelFactory
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.savedstate.SavedStateRegistry
//import androidx.savedstate.SavedStateRegistryOwner
//import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
//import com.example.vetclinic.presentation.screens.updatePasswordScreen.UpdatePasswordViewModel
//import jakarta.inject.Inject
//
//class UpdatePasswordViewModelFactory (
//    private val resetPasswordUseCase: ResetPasswordUseCase,
//    owner: SavedStateRegistryOwner,
//    defaultArgs:Bundle? = null
//): AbstractSavedStateViewModelFactory(owner, defaultArgs) {
//
//    override fun <T : ViewModel> create(
//        key: String,
//        modelClass: Class<T>,
//        handle: SavedStateHandle,
//    ): T {
//        return when {
//            modelClass.isAssignableFrom(UpdatePasswordViewModel::class.java) -> {
//                UpdatePasswordViewModel(resetPasswordUseCase, handle) as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel Class: $modelClass")
//        }
//    }
//}