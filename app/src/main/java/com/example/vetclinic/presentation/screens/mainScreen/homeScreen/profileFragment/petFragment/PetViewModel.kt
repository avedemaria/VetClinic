package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.utils.AgeUtils
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val sessionUseCase: SessionUseCase
) : ViewModel() {

    private val _petState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val petState: StateFlow<PetUiState> get() = _petState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> get() = _toastEvent.asSharedFlow()

    init {
        getPetsData()
    }

    private fun getPetsData() {
        viewModelScope.launch {
            sessionUseCase.getUserId()?.let { userId ->
                _petState.value = PetUiState.Loading

                petUseCase.getPetsFromRoom(userId).fold(
                    onSuccess = { pets ->
                        _petState.value = PetUiState.Success(pets)
                    },
                    onFailure = { e ->
                        _petState.value = PetUiState.Error(e.message ?: "Неизвестная ошибка")
                    }
                )
            } ?: run {
                _petState.value = PetUiState.Error("Пользователь не найден")
            }
        }
    }

    fun updatePet(petId: String, updatedPet: Pet) {
        viewModelScope.launch {
            val currentState = _petState.value
            if (currentState is PetUiState.Success) {
                val updatedList =
                    currentState.pets.map { if (it.petId == petId) updatedPet else it }
                _petState.value = PetUiState.Loading
                _petState.value = PetUiState.Success(updatedList)

                petUseCase.updatePetInSupabaseDb(petId, updatedPet).onFailure { e ->
                    _petState.value = PetUiState.Success(currentState.pets)
                    _petState.value = PetUiState.Error(e.message ?: "Ошибка обновления питомца")
                }
            }
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            val currentState = _petState.value
            if (currentState is PetUiState.Success) {
                val updatedPets = currentState.pets.filter { it.petId != pet.petId }
                _petState.value = PetUiState.Success(updatedPets)

                petUseCase.deletePetFromSupabaseDb(pet).fold(
                    onSuccess = {
                        _toastEvent.emit("Питомец удален")
                        Log.d(TAG, "deleted pet from room and supabase successfully")
                    },
                    onFailure = { e ->
                        _petState.value = PetUiState.Success(currentState.pets)
                        _petState.value = PetUiState.Error(e.message ?: "Ошибка удаления питомца")
                    }
                )
            }
        }
    }


    companion object {
        private const val TAG = "PetViewModel"
    }
}