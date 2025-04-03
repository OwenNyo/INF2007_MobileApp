package com.example.inf2007_proj.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.inf2007_proj.Activity.MedicationReminder.MedicationActivity
import com.example.inf2007_proj.DataManager.MedicationDataManager
import com.example.inf2007_proj.DataModels.MedicationEntity
import com.example.inf2007_proj.R
import com.example.inf2007_proj.Session.Session

class MedicationDialogFragment : DialogFragment() {

    interface MedicationDialogListener {
        fun onMedicationUpdatedOrAdded()
    }

    private var listener: MedicationDialogListener? = null
    private var medication: MedicationEntity? = null
    private var isEditMode = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is MedicationDialogListener) {
            listener = parentFragment as MedicationDialogListener
        } else {
            Log.w("MedicationDialogFragment", "Parent fragment does not implement MedicationDialogListener. This is expected if using local mode.")
        }
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medication = arguments?.getParcelable("medication")
        isEditMode = medication != null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater

        // Wrap the entire dialog view in a ScrollView for scrolling
        val scrollView = ScrollView(requireContext())
        val view = inflater.inflate(R.layout.fragment_medication_dialog, null)
        scrollView.addView(view)

        val nameEditText = view.findViewById<EditText>(R.id.edit_text_name)
        val frequencySpinner = view.findViewById<Spinner>(R.id.spinner_frequency)
        val timeSelectorContainer = view.findViewById<LinearLayout>(R.id.time_selector_container)
        val dateStartEditText = view.findViewById<EditText>(R.id.edit_text_date_start)
        val dateEndEditText = view.findViewById<EditText>(R.id.edit_text_date_end)

        val appContext = context
        val isLocalMode = arguments?.getBoolean("isLocalMode", false) ?: false // NEW: Get mode

        // Populate the frequency dropdown
        val frequencyOptions = listOf("1 Time a Day", "2 Times a Day", "3 Times a Day")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter

        if (isEditMode) {
            medication?.let {
                Log.d("DEBUG", "Populating fields for medication: $it")
                Log.d("DEBUG", "Frequency: ${medication?.frequency}")
                Log.d("DEBUG", "Dose Times: ${listOf(medication?.firstDoseTime, medication?.secondDoseTime, medication?.thirdDoseTime)}")
                Log.d("DEBUG", "Start Date: ${it.dateStart}, End Date: ${it.dateEnd}")
                nameEditText.setText(it.med_name)

                val normalizedFrequency = it.frequency.lowercase().trim()
                val normalizedOptions = frequencyOptions.map { option -> option.lowercase().trim() }
                val frequencyIndex = normalizedOptions.indexOf(normalizedFrequency)
                // Set Frequency
                // Populate Frequency
                if (frequencyIndex >= 0) {
                    frequencySpinner.setSelection(frequencyIndex)
                } else {
                    Log.d("DEBUG", "Frequency not found in options: ${it.frequency}")
                }

                // Populate Dates
                dateStartEditText.setText(it.dateStart.takeIf { date -> date != "NA" } ?: "")
                dateEndEditText.setText(it.dateEnd.takeIf { date -> date != "NA" } ?: "")

                timeSelectorContainer.post {
                    populateTimeSelectors(
                        it.frequency,
                        timeSelectorContainer,
                        listOf(it.firstDoseTime ?: "00:00", it.secondDoseTime ?: "00:00", it.thirdDoseTime ?: "00:00")
                    )
                }
            }
        }

        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val frequency = frequencyOptions[position]
                updateTimeSelectors(frequency, timeSelectorContainer)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where no item is selected (optional)
            }
        }

        builder.setView(scrollView) // Set the scrollable view as the dialog content
            .setTitle(if (isEditMode) "Edit Medication" else "Add Medication")
            .setPositiveButton(if (isEditMode) "Save" else "Add") { _, _ ->
                val medName = nameEditText.text.toString()
                val frequency = frequencySpinner.selectedItem.toString()
                val dateStart = dateStartEditText.text.toString()
                val dateEnd = dateEndEditText.text.toString()
                val timeSelections = getTimeSelections(timeSelectorContainer)

                if (medName.isNotEmpty() && frequency.isNotEmpty() && dateStart.isNotEmpty() && dateEnd.isNotEmpty() && timeSelections.isNotEmpty()) {
                    val session = Session(requireContext())
                    val username = session.getUsername()

                    val updatedMedication = medication?.copy(
                        med_name = medName,
                        frequency = frequency,
                        firstDoseTime = timeSelections.getOrNull(0) ?: "NA",
                        secondDoseTime = timeSelections.getOrNull(1) ?: "NA",
                        thirdDoseTime = timeSelections.getOrNull(2) ?: "NA",
                        dateStart = dateStart,
                        dateEnd = dateEnd
                    ) ?: MedicationEntity(
                        id = 0,
                        username = username,
                        med_name = medName,
                        frequency = frequency,
                        firstDoseTime = timeSelections.getOrNull(0) ?: "NA",
                        secondDoseTime = timeSelections.getOrNull(1) ?: "NA",
                        thirdDoseTime = timeSelections.getOrNull(2) ?: "NA",
                        dateStart = dateStart,
                        dateEnd = dateEnd
                    )

                    if (isLocalMode) {
                        // Local update only
                        sendResult("saveResult", updatedMedication)
                    } else {
                        val medicationDataManager = MedicationDataManager()
                        val callback: (Boolean) -> Unit = { success ->
                            appContext?.let { safeContext ->
                                if (success) {
                                    Toast.makeText(
                                        safeContext,
                                        "Medication ${if (isEditMode) "updated" else "added"} successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(safeContext, MedicationActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    safeContext.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        safeContext,
                                        "Failed to ${if (isEditMode) "update" else "add"} medication.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        if (isEditMode) {
                            medicationDataManager.updateMedication(updatedMedication, callback)
                        } else {
                            medicationDataManager.insertMedication(updatedMedication, callback)
                        }
                    }
                } else {
                    appContext?.let { safeContext ->
                        Toast.makeText(safeContext, "All fields are required!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)

        // 🔹 Only add the "Delete" button if we are in Edit Mode
        if (isEditMode) {
            builder.setNeutralButton("Delete") { _, _ ->
                if (isLocalMode) {
                    medication?.let { medToDelete ->
                        sendResult("deleteResult", medToDelete)
                    }
                }
                else {
                    medication?.let { medToDelete ->
                        val medicationDataManager = MedicationDataManager()
                        medicationDataManager.deleteMedication(medToDelete) { success ->
                            appContext?.let { safeContext ->
                                if (success) {
                                    Toast.makeText(
                                        safeContext,
                                        "Medication deleted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    listener?.onMedicationUpdatedOrAdded()
                                    val intent = Intent(safeContext, MedicationActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    safeContext.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        safeContext,
                                        "Failed to delete medication.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        return builder.create()
    }

    private fun sendResult(resultKey: String, medication: MedicationEntity?) {
        val result = Bundle().apply {
            putParcelable("medicationResult", medication)
        }
        parentFragmentManager.setFragmentResult(resultKey, result)
    }


    private fun updateTimeSelectors(frequency: String, container: LinearLayout) {
        container.removeAllViews()
        val times = when (frequency.lowercase()) {
            "1 time a day" -> 1
            "2 times a day" -> 2
            "3 times a day" -> 3
            else -> 0
        }

        val headers = listOf(
            "Set First Dose Time (24Hr)",
            "Set Second Dose Time (24Hr)",
            "Set Third Dose Time (24Hr)"
        )

        for (i in 0 until times) {
            val header = TextView(requireContext()).apply {
                text = headers[i]
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }

            val timePickerLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }

            // Hour Picker with HH Label
            val hourLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(10, 0, 10, 0)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val hourLabel = TextView(requireContext()).apply {
                text = "HH"
                textSize = 16f
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val hourPicker = NumberPicker(requireContext()).apply {
                minValue = 0
                maxValue = 23
                wrapSelectorWheel = true
                setFormatter { i -> String.format("%02d", i) }
            }

            hourLayout.addView(hourPicker)
            hourLayout.addView(hourLabel)

            // Minute Picker with MM Label
            val minuteLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(10, 0, 10, 0)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val minuteLabel = TextView(requireContext()).apply {
                text = "MM"
                textSize = 16f
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val minutePicker = NumberPicker(requireContext()).apply {
                minValue = 0
                maxValue = 59
                wrapSelectorWheel = true
                setFormatter { i -> String.format("%02d", i) }
            }

            minuteLayout.addView(minutePicker)
            minuteLayout.addView(minuteLabel)

            // Combine layouts
            timePickerLayout.addView(hourLayout)
            timePickerLayout.addView(minuteLayout)

            container.addView(header)
            container.addView(timePickerLayout)
        }
    }

    private fun populateTimeSelectors(frequency: String, container: LinearLayout, times: List<String>) {
        updateTimeSelectors(frequency, container)
        var timeIndex = 0
        for (i in 0 until container.childCount) {
            val childView = container.getChildAt(i)
            if (childView is LinearLayout) {
                val hourLayout = childView.getChildAt(0) as? LinearLayout
                val minuteLayout = childView.getChildAt(1) as? LinearLayout

                if (hourLayout != null && minuteLayout != null) {
                    val hourPicker = hourLayout.getChildAt(0) as? NumberPicker
                    val minutePicker = minuteLayout.getChildAt(0) as? NumberPicker

                    if (hourPicker != null && minutePicker != null && timeIndex < times.size) {
                        val timeParts = times[timeIndex].split(":").map { it.toInt() }
                        Log.d("MedicationDialogFragment", "Setting dose ${timeIndex + 1}: $timeParts")

                        // Set the values directly
                        hourPicker.value = timeParts[0]
                        minutePicker.value = timeParts[1]

                        timeIndex++
                    }
                }
            }
        }
    }


    private fun getTimeSelections(container: LinearLayout): List<String> {
        val times = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val childView = container.getChildAt(i)
            if (childView is LinearLayout && childView.childCount == 2) {
                val hourPicker = (childView.getChildAt(0) as LinearLayout).getChildAt(0) as NumberPicker
                val minutePicker = (childView.getChildAt(1) as LinearLayout).getChildAt(0) as NumberPicker
                val hour = hourPicker.value
                val minute = minutePicker.value
                times.add(String.format("%02d:%02d", hour, minute))
            }
        }
        return times
    }


    companion object {
        fun newInstance(medication: MedicationEntity? = null, isLocalMode: Boolean = false): MedicationDialogFragment {
            val fragment = MedicationDialogFragment()
            val bundle = Bundle()
            medication?.let { bundle.putParcelable("medication", it) }
            bundle.putBoolean("isLocalMode", isLocalMode) // NEW: Pass mode
            fragment.arguments = bundle
            return fragment
        }
    }

}
