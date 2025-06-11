package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val sessionUseCase: SessionUseCase,
) : ViewModel() {

    private val _petState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val petState: StateFlow<PetUiState> get() = _petState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


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
                        _petState.value = PetUiState.Error
                        _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
                    }
                )
            } ?: run {
                _petState.value = PetUiState.Error
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
                    _petState.value = PetUiState.Error
                    _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
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
                        _uiEvent.emit(UiEvent.ShowSnackbar("Питомец удален"))

                    },
                    onFailure = { e ->
                        _petState.value = PetUiState.Success(currentState.pets)
                        _petState.value = PetUiState.Error
                        _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
                    }
                )
            }
        }
    }


    companion object {
        private const val TAG = "PetViewModel"
    }
}