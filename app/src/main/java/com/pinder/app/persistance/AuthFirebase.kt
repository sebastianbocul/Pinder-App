package com.pinder.app.persistance

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pinder.app.util.Resource

class AuthFirebase constructor(context: Context) {
    val isUserInFirebase: MutableLiveData<Resource<Boolean>> = MutableLiveData();

    fun checkUserInFirebase(): LiveData<Resource<Boolean>> {
        isUserInFirebase.postValue(Resource.loading(null))
        val currentUID = FirebaseAuth.getInstance().currentUser!!.uid
        val currentUserDatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUID)
        currentUserDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                return isUserInFirebase.postValue(Resource.success(dataSnapshot.exists()))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                return isUserInFirebase.postValue(Resource.error("ERROR", null))
            }
        })
        return isUserInFirebase
    }
}