package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.DeletePetUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.UpdatePetUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _petState = MutableLiveData<PetUiState>()
    val petState: LiveData<PetUiState> get() = _petState


     fun getPetsData() {
         viewModelScope.launch {
             val userId = userDataStore.getUserId()

             Log.d(TAG, "userId: $userId")

             if (userId != null) {
                 _petState.value = PetUiState.Loading

                 Log.d(TAG, "userId: $userId")
                 val result = getPetsUseCase.getPetsFromRoom(userId)
                 Log.d(TAG, "Result: $result")
                 if (result.isSuccess) {
                     val pets = result.getOrThrow()

                     if (pets.isEmpty()) {
                         return@launch
                     }
                     _petState.value = PetUiState.Success(pets)
                 } else {
                     _petState.value = PetUiState.Error(
                         result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                     )
                 }
             }
         }
    }


    fun updatePet(petId: String, updatedPet: Pet) {
        _petState.value = PetUiState.Loading

        viewModelScope.launch {
            val supabaseResult = updatePetUseCase.updatePetInSupabaseDb(petId, updatedPet)

            if (supabaseResult.isSuccess) {
                getPetsData()
            } else {
                val errorMessage = supabaseResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(TAG, "Error while updating pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }

    fun deletePet(pet: Pet) {
        _petState.value = PetUiState.Loading



        viewModelScope.launch {

            val result = deletePetUseCase.deletePetFromSupabaseDb(pet)

            if (result.isSuccess) {
                _petState.value = PetUiState.Deleted
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(TAG, "Error while deleting pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }


    companion object {
        const val TAG = "PetViewModel"
    }

}