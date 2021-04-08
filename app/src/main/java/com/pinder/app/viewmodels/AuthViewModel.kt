package com.pinder.app.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pinder.app.repository.AuthRepository
import com.pinder.app.util.Resource

class AuthViewModel @ViewModelInject constructor(private val authRepository: AuthRepository) : ViewModel() {
    fun fetchUserData(): LiveData<Resource<Boolean>> {
        return authRepository.fetchUserData()
    }
}