package com.pinder.app.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pinder.app.repository.AuthRepository
import com.pinder.app.repository.MatchesRepository
import com.pinder.app.util.Resource

class CommunicationViewModel @ViewModelInject constructor(): ViewModel(){
    var matchProfileUrl: String? =null
    var matchName: String? = null
    var matchId: String? = null
    var fromActivity: String? =null
}