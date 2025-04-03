package com.example.inf2007_proj.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inf2007_proj.Adapters.MedicationAdapter
import com.example.inf2007_proj.DataModels.MedicationEntity
import com.example.inf2007_proj.R
import com.example.inf2007_proj.Session.Session
import com.example.inf2007_proj.ViewModel.ScanMedsViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NewScanMedListFragment : Fragment(), MedicationDialogFragment.MedicationDialogListener {

    private lateinit var captureButton: Button
    private lateinit var saveAllButton: Button
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicationAdapter
    private lateinit var backPressCallback: OnBackPressedCallback
    private var photoFile: File? = null
    private val viewModel: ScanMedsViewModel by viewModels()

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_scan_med_list, container, false)

        captureButton = view.findViewById(R.id.captureButton)
        saveAllButton = view.findViewById(R.id.saveAllButton)
        imageView = view.findViewById(R.id.imageView)
        recyclerView = view.findViewById(R.id.recyclerViewMedications)

        adapter = MedicationAdapter(emptyList()) { medication -> showEditDialog(medication) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        captureButton.setOnClickListener { dispatchTakePictureIntent() }
        saveAllButton.setOnClickListener { savePrescriptionToDatabase() }

        // Observe ViewModel for medication updates
        viewModel.medications.observe(viewLifecycleOwner) { medications ->
            adapter.updateData(medications)
        }

        // Handle Save Button (Updating/Adding Medication)
        parentFragmentManager.setFragmentResultListener("saveResult", this) { _, bundle ->
            val medication = bundle.getParcelable<MedicationEntity>("medicationResult")
            medication?.let {
                val existingMeds = viewModel.medications.value ?: emptyList()
                val existingIndex = existingMeds.indexOfFirst { it.id == medication.id }

                if (existingIndex >= 0) {
                    // Update existing medication
                    viewModel.updateMedication(medication)
                } else {
                    // Add new medication
                    viewModel.addMedication(medication.copy(id = (existingMeds.maxOfOrNull { it.id } ?: 0) + 1))
                }
            }
        }

        // Handle Delete Button (Removing Medication)
        parentFragmentManager.setFragmentResultListener("deleteResult", this) { _, bundle ->
            val medication = bundle.getParcelable<MedicationEntity>("medicationResult")
            medication?.let { viewModel.deleteMedication(it) }
        }

        // Handle back press confirmation
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.medications.value?.isNotEmpty() == true) {
                    showExitConfirmationDialog()
                } else {
                    backPressCallback.isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallback)

        return view
    }

    private fun savePrescriptionToDatabase() {
        if (viewModel.medications.value.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No medications to save.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.saveAllMedications {
            Toast.makeText(requireContext(), "All medications saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Unsaved Medications")
            .setMessage("You have unsaved medications. Do you want to save them before exiting?")
            .setPositiveButton("Save") { _, _ ->
                savePrescriptionToDatabase {
                    //Navigate back only after the save completes
                    redirectToHomePage()
                }
            }
            .setNegativeButton("Exit Without Saving") { _, _ ->
                backPressCallback.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .show()
    }

    //Modified save function with a callback
    private fun savePrescriptionToDatabase(onComplete: () -> Unit) {
        if (viewModel.medications.value.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No medications to save.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.saveAllMedications {
            Toast.makeText(requireContext(), "All medications saved!", Toast.LENGTH_SHORT).show()
            onComplete() //Callback after saving
        }
    }

    //Function to navigate to Home Page
    private fun redirectToHomePage() {
        backPressCallback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }


    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.fileprovider", it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_", ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            photoFile?.let {
                val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                imageView.setImageBitmap(bitmap)
                processImageForOCR(it)
            }
        }
    }

    private fun processImageForOCR(imageFile: File) {
        lifecycleScope.launch(Dispatchers.Default) { // Run OCR in background
            try {
                val image = context?.let { InputImage.fromFilePath(it, Uri.fromFile(imageFile)) }
                if (image == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val extractedText = recognizeTextFromImage(image) // Now uses coroutine-safe function

                val medicineName = extractMedicineNameUsingRegex(extractedText)
                val frequency = extractDosageUsingRegex(extractedText)

                val session = context?.let { Session(it) } // Ensure context is available
                val username = session?.getUsername() ?: "Unknown User"

                val newId = (viewModel.medications.value?.maxOfOrNull { it.id } ?: 0) + 1
                val medication = MedicationEntity(
                    id = newId,
                    username = username,
                    med_name = medicineName,
                    frequency = frequency,
                    dateStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    dateEnd = "2025-12-31",
                    firstDoseTime = "09:00"
                )

                withContext(Dispatchers.Main) {
                    if (isAdded) { //Check if Fragment is attached before updating UI
                        viewModel.addMedication(medication)
                        Toast.makeText(requireContext(), "Medication added: $medicineName", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) { //Prevent crash if Fragment is detached
                        Toast.makeText(requireContext(), "OCR Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private suspend fun recognizeTextFromImage(image: InputImage): String {
        return suspendCancellableCoroutine { continuation ->
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text) // Resume coroutine with OCR result
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e) // Resume coroutine with error
                }
        }
    }

    private fun extractMedicineNameUsingRegex(text: String): String {
        val medicinePattern = Regex("""(?i)([A-Za-z]+[\s-]?[A-Za-z0-9]+)\s*(\d+mg|\d+ml|\d+g)""")
        return medicinePattern.find(text)?.value?.trim() ?: "Unknown Medicine"
    }

    private fun extractDosageUsingRegex(text: String): String {
        val dosagePattern = Regex("""(?i)(take\s*\d+\s*tablet[s]?|take\s*\d+\s*tab[s]?)([^.,\n]*)""")
        return dosagePattern.find(text)?.value?.trim() ?: "Dosage instructions not detected"
    }

    private fun showEditDialog(medication: MedicationEntity) {
        val dialog = MedicationDialogFragment.newInstance(medication, isLocalMode = true)
        dialog.show(parentFragmentManager, "MedicationDialogFragment")
    }

    override fun onMedicationUpdatedOrAdded() {
        adapter.notifyDataSetChanged()
    }
}
