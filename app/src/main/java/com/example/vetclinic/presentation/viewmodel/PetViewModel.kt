package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.AddUserUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.UpdatePetUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val addUserUseCase: AddUserUseCase
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
            val supabaseResult = updatePetUseCase.updatePetInSupabaseDb(petId, updatedPet)

            if (supabaseResult.isSuccess) {
                val roomResult = updatePetUseCase.updatePetInRoom(updatedPet)
                if (roomResult.isSuccess) {
                    Log.d("PetViewModel", "Pet successfully updated in Room")
                } else {
                    Log.e(
                        "PetViewModel", "Error updating pet in Room: " +
                                "${roomResult.exceptionOrNull()?.message}"
                    )
                }
                getPetsFromRoom(userId)
            } else {
                val errorMessage = supabaseResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e("PetViewModel", "Error while updating pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }

    fun addPet(pet: Pet, userId: String) {
        _petState.value = PetUiState.Loading

        viewModelScope.launch {

            val supabaseResult = addUserUseCase.addPetToSupabaseDb(pet)
            if (supabaseResult.isSuccess) {
                getPetsFromRoom(userId)
            } else {
                _petState.value = PetUiState.Error(
                    supabaseResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                )
            }
        }
    }


}