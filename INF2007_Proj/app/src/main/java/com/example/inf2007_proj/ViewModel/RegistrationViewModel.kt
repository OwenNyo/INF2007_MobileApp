package com.example.inf2007_proj.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_proj.DataManager.RegistrationDataManager
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val registrationDataManager = RegistrationDataManager()
    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> get() = _registrationStatus

    // **Refactored to call `suspend` function properly**
    fun registerUser(username: String, password: String, contactNumber: String, emailAddress: String, houseAddress: String, postalCode: String) {
        viewModelScope.launch {
            // First, check if username is taken
            val isTaken = registrationDataManager.isUsernameTaken(username)
            if (isTaken) {
                _registrationStatus.postValue(false) // Username already exists
                return@launch
            }

            // Proceed with registration if username is available
            val success = registrationDataManager.registerUser(username, password, contactNumber, emailAddress, houseAddress, postalCode)
            _registrationStatus.postValue(success)
        }
    }
}
