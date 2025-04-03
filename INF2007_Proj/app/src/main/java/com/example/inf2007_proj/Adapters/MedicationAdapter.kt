package com.example.inf2007_proj.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inf2007_proj.DataModels.MedicationEntity
import com.example.inf2007_proj.R
import java.text.SimpleDateFormat
import java.util.Locale

class MedicationAdapter(
    private var medications: List<MedicationEntity>,
    private val onEditClick: (MedicationEntity) -> Unit
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    inner class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.medication_name)
        val frequencyTextView: TextView = view.findViewById(R.id.medication_frequency)
        val firstDoseTextView: TextView = view.findViewById(R.id.medication_first_dose)
        val secondDoseTextView: TextView = view.findViewById(R.id.medication_second_dose)
        val thirdDoseTextView: TextView = view.findViewById(R.id.medication_third_dose)
        val dateStartTextView: TextView = view.findViewById(R.id.medication_date_start)
        val dateEndTextView: TextView = view.findViewById(R.id.medication_date_end)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication_recycleview, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = medications[position]
        holder.nameTextView.text = medication.med_name
        holder.frequencyTextView.text = "Frequency: ${medication.frequency}"

        // Set dose times
        holder.firstDoseTextView.text = "First Dose: ${medication.firstDoseTime ?: "NA"}"
        holder.secondDoseTextView.text = "Second Dose: ${medication.secondDoseTime ?: "NA"}"
        holder.thirdDoseTextView.text = "Third Dose: ${medication.thirdDoseTime ?: "NA"}"

        // Apply formatting for Start Date and End Date
        holder.dateStartTextView.text = "Start: ${formatDate(medication.dateStart)}"
        holder.dateEndTextView.text = "End: ${formatDate(medication.dateEnd)}"

        holder.itemView.findViewById<View>(R.id.fab_edit_medication).setOnClickListener {
            onEditClick(medication)
        }
    }

    override fun getItemCount(): Int = medications.size

    fun updateData(newMedications: List<MedicationEntity>) {
        println("DEBUG: Adapter updating data with ${newMedications.size} items")
        medications = newMedications
        notifyDataSetChanged()
    }
}
