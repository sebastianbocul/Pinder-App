package com.pinder.app.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class CommunicationViewModel @ViewModelInject constructor() : ViewModel() {
    var matchProfileUrl: String? = null
    var matchName: String? = null
    var matchId: String? = null
    var fromActivity: String? = null
}