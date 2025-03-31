package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {


    private val _mainState = MutableLiveData<MainState>()
    val mainState: LiveData<MainState> get() = _mainState


    fun getUserIdAndFetchData() {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: return@launch
            Log.d("MainViewModel", "userId1 $userId")
            getUserAndPet(userId)
        }
    }

//
//    private fun getUserAndPet(userId: String) {
//
//        _mainState.value = MainState.Loading
//        viewModelScope.launch {
//
//            // TODO: Do it synchronously (order is important)
//            val userDeferred = async { getUserUseCase.getUserFromSupabaseDb(userId) }
//            val petDeferred = async { getPetsUseCase.getPetsFromSupabaseDb(userId) }
//
//            val userResult = userDeferred.await()
//            val petResult = petDeferred.await()
//
//            when {
//                userResult.isSuccess && petResult.isSuccess -> {
//                    val user = userResult.getOrNull()
//                    val pets = petResult.getOrNull()
//                    if (user != null && !pets.isNullOrEmpty()) {
//                        _mainState.value = MainState.Result(user, pets)
//                    }
//                }
//
//                userResult.isFailure -> {
//                    _mainState.value =
//                        MainState.Error(
//                            "Ошибка загрузки пользователя: " +
//                                    "${userResult.exceptionOrNull()?.message}"
//                        )
//                }
//
//                petResult.isFailure -> {
//                    _mainState.value =
//                        MainState.Error(
//                            "Ошибка загрузки питомца: " +
//                                    "${petResult.exceptionOrNull()?.message}"
//                        )
//                }
//            }
//        }
//    }

    private suspend fun getUserAndPet(userId: String) {
        _mainState.value = MainState.Loading

        val userResult = getUserUseCase.getUserFromSupabaseDb(userId)
        if (userResult.isSuccess) {
            fetchAndProcessPetData(userId, userResult.getOrNull())
        } else {
            _mainState.value = MainState.Error(
                "Ошибка загрузки пользователя: " +
                        "${userResult.exceptionOrNull()?.message}"
            )
        }
    }


private suspend fun fetchAndProcessPetData(userId: String, user: User?) {

    if (user == null) {
        _mainState.value = MainState.Error("User is null")
        return
    }

    val petResult = getPetsUseCase.getPetsFromSupabaseDb(userId)

    when {
        petResult.isSuccess -> {
            val pets = petResult.getOrNull()
            if (!pets.isNullOrEmpty()) {
                Log.d("MainViewModel", "pets: $pets")
                _mainState.value = MainState.Result(user, pets)
            } else {
                _mainState.value = MainState.Error("No pets found")
            }
        }

        petResult.isFailure -> {
            _mainState.value = MainState.Error(
                "Ошибка загрузки питомца: " +
                        "${petResult.exceptionOrNull()?.message}"
            )
        }
    }
}
}









