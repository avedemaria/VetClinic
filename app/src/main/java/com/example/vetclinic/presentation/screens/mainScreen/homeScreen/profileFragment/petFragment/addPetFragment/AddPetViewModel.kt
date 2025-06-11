package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddPetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val sessionUseCase: SessionUseCase,

    ) : ViewModel() {


    private var userId: String? = null

    private val _addPetState = MutableLiveData<AddPetUiState>()
    val addPetState: LiveData<AddPetUiState> get() = _addPetState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    init {
        viewModelScope.launch {
            userId = sessionUseCase.getUserId()
        }
    }

    fun addPetData(petName: String, petType: String, petGender: String, petBDay: String) {

        val currentUserId = userId
        val petId = UUID.randomUUID().toString()

        viewModelScope.launch {
            if (currentUserId == null) {
                _addPetState.value = AddPetUiState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar("UserId отсутствует"))
                return@launch
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

            val result = petUseCase.addPetToSupabaseDb(pet)

            if (result.isSuccess) {
                petUseCase.addPetToRoom(pet)
                _addPetState.value = AddPetUiState.Success
            } else {
                val errorMessage = result.exceptionOrNull()?.message
                _addPetState.value = AddPetUiState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(errorMessage.toString()))
            }
        }
    }


}