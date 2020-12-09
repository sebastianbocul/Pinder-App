package com.pinder.app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.pinder.app.cache.AuthCache
import com.pinder.app.persistance.AuthFirebase
import com.pinder.app.util.AppExecutors
import com.pinder.app.util.ConstantNetworkBoundResource
import com.pinder.app.util.NetworkBoundResource
import com.pinder.app.util.Resource

class AuthRepository constructor(val authFirebase: AuthFirebase, val authCache: AuthCache) {
    private val TAG = "AuthRepository"
    fun fetchUserData(): LiveData<Resource<Boolean>> {
        return object : ConstantNetworkBoundResource<Boolean, Boolean>(AppExecutors.getInstance()) {
            override fun saveFirebaseResult(item: Boolean) {
                Log.d(TAG, "saveFirebaseRes $item" )
                authCache.saveUserToCache()
            }

            override fun shouldFetch(data: Boolean?): Boolean {
                Log.d(TAG, "shouldFetch " + !data!!)
                return !data!!
            }

            override fun loadFromDb(): LiveData<Boolean> {
                Log.d(TAG, "loadFromDb")
                return authCache.checkUserInCache()
            }

            override fun createFirebaseCall(): LiveData<Resource<Boolean>> {
                Log.d(TAG, "createFirebaseCall")
                return authFirebase.checkUserInFirebase()
            }
        }.asLiveData
    }
}