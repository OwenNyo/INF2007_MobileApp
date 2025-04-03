package com.example.inf2007_proj.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_proj.DataManager.LoginDataManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginDataManager = LoginDataManager()
    private val _loginStatus = MutableLiveData<String?>()
    val loginStatus: LiveData<String?> get() = _loginStatus

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val status = loginDataManager.getLoginStatus(username, password)
            _loginStatus.postValue(status)
        }
    }
}
