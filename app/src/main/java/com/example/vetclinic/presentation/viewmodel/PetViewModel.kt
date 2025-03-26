package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.AddUserUseCase
import com.example.vetclinic.domain.usecases.DeletePetUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.UpdatePetUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PetViewModel @Inject constructor(
    private val getPetsUseCase: GetPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _petState = MutableLiveData<PetUiState>()
    val petState: LiveData<PetUiState> get() = _petState


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: return@launch
            Log.d("PetViewModel", "userId: $userId")
//            val petDeferred = async { getPetsUseCase.getPetsFromSupabaseDb(userId) }

            getPetsData(userId)
        }

    }

    private suspend fun getPetsData(userId: String) {

        _petState.value = PetUiState.Loading
        val result = getPetsUseCase.getPetsFromRoom(userId)
        Log.d("PetViewModel", "Result: $result")
        if (result.isSuccess) {
            val pets = result.getOrThrow()

            if (pets.isEmpty()) {
                return
            }
            _petState.value = PetUiState.Success(pets)
        } else {
            _petState.value = PetUiState.Error(
                result
                    .exceptionOrNull()?.message ?: "Неизвестная ошибка"
            )
        }
    }


    fun updatePet(userId: String, petId: String, updatedPet: Pet) {
        _petState.value = PetUiState.Loading

        viewModelScope.launch {
            val supabaseResult = updatePetUseCase.updatePetInSupabaseDb(petId, updatedPet)

            if (supabaseResult.isSuccess) {
                getPetsData(userId)
            } else {
                val errorMessage = supabaseResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e("PetViewModel", "Error while updating pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }

    fun deletePet(pet: Pet) {
        _petState.value = PetUiState.Loading



        viewModelScope.launch {
//            val userId = userDataStore.getUserId() ?: ""

            val result = deletePetUseCase.deletePetFromSupabaseDb(pet)

            if (result.isSuccess) {
                _petState.value = PetUiState.Deleted
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e("PetViewModel", "Error while deleting pet $errorMessage")
                _petState.value = PetUiState.Error(errorMessage)
            }
        }
    }


}