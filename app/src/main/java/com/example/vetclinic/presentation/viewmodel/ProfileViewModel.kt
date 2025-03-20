package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.launch

//class ProfileViewModel @Inject constructor(
//    private val userDataStore: UserDataStore
//) : ViewModel() {
//
//
//    private val _userId = MutableLiveData<String>()
//    val userId: LiveData<String> get() = _userId
//
//
//    init {
//        viewModelScope.launch {
//            val userId = userDataStore.getUserId() ?: return@launch
//            _userId.value = userId
//        }
//    }
//}