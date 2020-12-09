package com.pinder.app.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pinder.app.repository.AuthRepository
import com.pinder.app.repository.MatchesRepository
import com.pinder.app.util.Resource

class AuthViewModel @ViewModelInject constructor(val authRepository: AuthRepository): ViewModel(){
    val userData:LiveData<Resource<String>> = MutableLiveData()

    fun fetchUserData():LiveData<Resource<Boolean>>{
        return authRepository.fetchUserData()
    }
}