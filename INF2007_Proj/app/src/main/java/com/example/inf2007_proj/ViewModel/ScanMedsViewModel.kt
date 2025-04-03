package com.example.inf2007_proj.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inf2007_proj.DataManager.MedicationDataManager
import com.example.inf2007_proj.DataModels.MedicationEntity
import kotlinx.coroutines.launch

class ScanMedsViewModel : ViewModel() {

    private val medicationDataManager = MedicationDataManager()

    // LiveData to store the list of scanned medications
    private val _medications = MutableLiveData<List<MedicationEntity>>(emptyList())
    val medications: LiveData<List<MedicationEntity>> get() = _medications

    // Add a new medication to the list
    fun addMedication(medication: MedicationEntity) {
        val updatedList = _medications.value?.toMutableList() ?: mutableListOf()
        updatedList.add(medication.copy(id = (updatedList.maxOfOrNull { it.id } ?: 0) + 1)) // Ensure unique ID
        _medications.value = updatedList
    }

    // Update an existing medication
    fun updateMedication(medication: MedicationEntity) {
        val updatedList = _medications.value?.map { if (it.id == medication.id) medication else it }
        _medications.value = updatedList ?: listOf(medication)
    }

    // Delete a medication from the list
    fun deleteMedication(medication: MedicationEntity) {
        _medications.value = _medications.value?.filter { it.id != medication.id }
    }

    // Save all scanned medications to the database
    fun saveAllMedications(onComplete: () -> Unit) {
        viewModelScope.launch {
            val meds = _medications.value ?: return@launch
            if (meds.isEmpty()) return@launch

            var successCount = 0
            meds.forEach { medication ->
                medicationDataManager.insertMedication(medication) { success ->
                    if (success) {
                        successCount++
                        if (successCount == meds.size) {
                            _medications.postValue(emptyList()) // Clear the list after saving
                            onComplete()
                        }
                    }
                }
            }
        }
    }

    // Clear the scanned medications list (used after saving or when needed)
    fun clearMedications() {
        _medications.value = emptyList()
    }
}
