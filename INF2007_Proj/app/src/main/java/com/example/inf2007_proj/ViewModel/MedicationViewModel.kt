package com.example.inf2007_proj.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_proj.DataManager.MedicationDataManager
import com.example.inf2007_proj.DataModels.MedicationEntity
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {

    private val medicationDataManager = MedicationDataManager()

    // LiveData to hold medication list
    private val _medications = MutableLiveData<List<MedicationEntity>>()
    val medications: LiveData<List<MedicationEntity>> get() = _medications

    // Load medications for the logged-in user
    fun loadMedications(username: String) {
        viewModelScope.launch {
            medicationDataManager.getTodaysMedications(username) { medicationList ->
                _medications.postValue(medicationList)
            }
        }
    }

    // Add or update medication
    fun saveMedication(medication: MedicationEntity, isEdit: Boolean) {
        viewModelScope.launch {
            if (isEdit) {
                medicationDataManager.updateMedication(medication) { success ->
                    if (success) reloadMedications(medication.username)
                }
            } else {
                medicationDataManager.insertMedication(medication) { success ->
                    if (success) reloadMedications(medication.username)
                }
            }
        }
    }

    // Delete medication
    fun deleteMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            medicationDataManager.deleteMedication(medication) { success ->
                if (success) reloadMedications(medication.username)
            }
        }
    }

    // Reload medications after updates
    private fun reloadMedications(username: String) {
        medicationDataManager.getTodaysMedications(username) { medicationList ->
            _medications.postValue(medicationList)
        }
    }
}
