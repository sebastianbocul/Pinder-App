package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.pinder.app.models.MatchesObject
import com.pinder.app.util.Constants

class AuthCache constructor(var context: Context) {
    private var mDatabase: Database? = null
    private val TAG = "AuthCache"
    val mapper = ObjectMapper()
    val currentUID = FirebaseAuth.getInstance().currentUser?.uid
    val isUserInCache: MutableLiveData<Boolean> = MutableLiveData();

    init {
        init()
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    fun checkUserInCache(): LiveData<Boolean> {
        Log.d(TAG, "checkUserInCache")
        val matchesLD: MutableLiveData<ArrayList<MatchesObject>> = MutableLiveData()
        val matchesArrayList: ArrayList<MatchesObject> = arrayListOf()
        matchesLD.value = matchesArrayList
        Log.d(TAG, context.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            Log.d(TAG, "Database exists: ")
            if (mDatabase != null) {
                Log.d(TAG, "mDatabase !=null: ")
                val document: Document? = mDatabase!!.getDocument("auth")
                if (document != null) {
                    var auth: String? = document.getString("user")
                    Log.d(TAG, "Getting user auth from cache finished $auth")
                    Log.d(TAG, "(auth.equals(currentUID) " + auth.equals(currentUID))
                    isUserInCache.postValue((auth.equals(currentUID)))
                } else {
                    isUserInCache.postValue(false)
                }
            } else {
                isUserInCache.postValue(false)
            }
        } else {
            isUserInCache.postValue(false)
        }
        Log.d(TAG, "getNumbers: numbers:$matchesArrayList")
        return isUserInCache
    }

    fun saveUserToCache() {
        Log.d(TAG, "CACHE userId empty $currentUID")
        try {
            val mutableDoc = MutableDocument("auth")
            mutableDoc.setValue("user", currentUID)
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun saveUserToCache(currentUID:String) {
        Log.d(TAG, "CACHE userId string$currentUID")
        try {
            val mutableDoc = MutableDocument("auth")
            mutableDoc.setValue("user", currentUID)
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}