package com.example.vetclinic.presentation.viewmodel

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