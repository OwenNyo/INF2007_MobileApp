package com.example.inf2007_proj.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_proj.DataManager.SettingsDataManager
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val settingsDataManager = SettingsDataManager()
    private val _settingsData = MutableLiveData<Map<String, String?>?>()
    val settingsData: LiveData<Map<String, String?>?> get() = _settingsData

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    // **Load user settings (Now using suspend function)**
    fun loadSettings(username: String) {
        viewModelScope.launch {
            val data = settingsDataManager.getSettingsDetail(username)
            _settingsData.postValue(data)
        }
    }

    // **Update settings and notify UI of success/failure**
    fun updateSettings(username: String, password: String, contact: String, email: String, address: String, postal: String) {
        viewModelScope.launch {
            val success = settingsDataManager.updateSettingsDetail(username, password, contact, email, address, postal)
            _updateSuccess.postValue(success)
        }
    }
}
