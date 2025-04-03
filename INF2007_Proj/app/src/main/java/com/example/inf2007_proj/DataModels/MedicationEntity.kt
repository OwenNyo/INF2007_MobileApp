package com.example.inf2007_proj.DataModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicationEntity(
    val id: Int,
    val username: String,
    val med_name: String,
    val frequency: String,
    val dateStart: String,
    val dateEnd: String,
    val firstDoseTime: String,
    val secondDoseTime: String? = null,
    val thirdDoseTime: String? = null
) : Parcelable

