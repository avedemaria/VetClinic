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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _petState = MutableLiveData<PetUiState>()
    val petState: LiveData<PetUiState> get() = _petState


    private val _storedPets = MutableStateFlow<List<Pet>>(emptyList())
    val storedPets: StateFlow<List<Pet>> get() = _storedPets.asStateFlow()


    init {
        getPetsData()
    }

    private fun getPetsData() {
        viewModelScope.launch {
            val userId = userDataStore.getUserId()

            Log.d(TAG, "userId1: $userId")

            if (userId != null) {
                _petState.value = PetUiState.Loading

                Log.d(TAG, "userId1: $userId")
                val result = getPetsUseCase.getPetsFromRoom(userId)
                Log.d(TAG, "Result: $result")
                if (result.isSuccess) {
                    val pets = result.getOrThrow()
                    Log.d("PetViewModel", "Загружаем питомцев для userId: $userId  $pets")
                    _storedPets.value = pets
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
            val oldList = _storedPets.value

            _storedPets.update { currentPets ->
                currentPets.map { if (it.petId == petId) updatedPet else it }
            }
            _petState.value = PetUiState.Success(_storedPets.value)
            val supabaseResult = updatePetUseCase.updatePetInSupabaseDb(petId, updatedPet)
            if (supabaseResult.isFailure) {
                _storedPets.value = oldList
                val errorMessage = supabaseResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(TAG, "Error while updating pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }

    fun deletePet(pet: Pet) {
        _petState.value = PetUiState.Loading
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            val result = deletePetUseCase.deletePetFromSupabaseDb(pet, userId)

            if (result.isSuccess) {
                val pets = result.getOrNull() ?: emptyList()
                _petState.value = PetUiState.Deleted
                _petState.value = PetUiState.Success(pets)
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