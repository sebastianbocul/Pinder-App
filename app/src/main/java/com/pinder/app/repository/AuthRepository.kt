package com.pinder.app.repository

import androidx.lifecycle.LiveData
import com.pinder.app.persistance.AuthFirebase
import com.pinder.app.util.Resource

class AuthRepository constructor(val authFirebase: AuthFirebase) {
    private val TAG = "AuthRepository"
    fun fetchUserData(): LiveData<Resource<Boolean>> {
        return authFirebase.checkUserInFirebase()
    }

}