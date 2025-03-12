package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.UpdatePetUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase
) : ViewModel() {

    private val _petState = MutableLiveData<PetUiState>()
    val petState: LiveData<PetUiState> get() = _petState


//    init {
//        getPetsFromRoom(userId)  как реализовать это
//    }

    fun getPetsFromRoom(userId: String) {

        _petState.value = PetUiState.Loading

        viewModelScope.launch {
            val result = getPetsUseCase.getPetsFromRoom(userId)

            if (result.isSuccess) {
                val pets = result.getOrThrow()

                if (pets.isEmpty()) {
                    return@launch
                }

                _petState.value = PetUiState.Success(pets)
            } else {
                _petState.value = PetUiState.Error(
                    result
                        .exceptionOrNull()?.message ?: "Неизвестная ошибка"
                )
            }

        }
    }


    fun updatePet(userId: String, petId: String, updatedPet: Pet) {
        _petState.value = PetUiState.Loading

        viewModelScope.launch {
            val result = updatePetUseCase.updatePetInSupabaseDb(petId, updatedPet)

            if (result.isSuccess) {
                getPetsFromRoom(userId)
            } else {
                _petState.value = PetUiState.Error(
                    result.exceptionOrNull()?.message?:"Неизвестная ошибка"
                )
            }
        }
    }

//        fun addPet () {
//
//        }


}