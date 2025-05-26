package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.util.UUID

class AddPetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val sessionUseCase: SessionUseCase

    ) : ViewModel() {


    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _addPetState = MutableLiveData<AddPetUiState>()
    val addPetState: LiveData<AddPetUiState> get() = _addPetState


    init {
        viewModelScope.launch {
            _userId.value = sessionUseCase.getUserId()
        }
    }

    fun addPetData(petName: String, petType: String, petGender: String, petBDay: String) {

        val currentUserId = _userId.value
        val petId = UUID.randomUUID().toString()

        if (currentUserId == null) {
            _addPetState.value = AddPetUiState.Error("UserId отсутствует")
            return
        }


        val pet = Pet(
            petId = petId,
            userId = currentUserId,
            petName = petName,
            petBDay = petBDay,
            petType = petType,
            petGender = petGender
        )


        _addPetState.value = AddPetUiState.Loading

        viewModelScope.launch {
            val result = petUseCase.addPetToSupabaseDb(pet)

            if (result.isSuccess) {
                petUseCase.addPetToRoom(pet)
                _addPetState.value = AddPetUiState.Success
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _addPetState.value = AddPetUiState.Error(errorMessage)
                Log.e("AddPetViewModel", "Error adding pet: $errorMessage")
            }
        }
    }

}