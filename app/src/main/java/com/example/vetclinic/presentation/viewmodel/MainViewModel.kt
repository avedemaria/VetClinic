package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {


    private val _mainState = MutableLiveData<MainState>()
    val mainState: LiveData<MainState> get() = _mainState


    fun getUserAndPet(userId: String) {
        viewModelScope.launch {
            _mainState.value = MainState.Loading

            val userDeferred = async { getUserUseCase.getUserFromSupabaseDb(userId) }
            val petDeferred = async { getPetsUseCase.getPetsFromSupabaseDb(userId) }

            val userResult = userDeferred.await()
            val petResult = petDeferred.await()

            when {
                userResult.isSuccess && petResult.isSuccess -> {
                    val user = userResult.getOrNull()
                    val pets = petResult.getOrNull()
                    if (user != null && !pets.isNullOrEmpty()) {
                        _mainState.value = MainState.Result(user, pets)
                    }
                }

                userResult.isFailure -> {
                    _mainState.value =
                        MainState.Error(
                            "Ошибка загрузки пользователя: " +
                                    "${userResult.exceptionOrNull()?.message}"
                        )
                }

                petResult.isFailure -> {
                    _mainState.value =
                        MainState.Error(
                            "Ошибка загрузки питомца: " +
                                    "${petResult.exceptionOrNull()?.message}"
                        )
                }
            }
        }

    }
}









