package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject

class UserProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
): ViewModel() {


    
}