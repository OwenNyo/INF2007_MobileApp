package com.example.inf2007_proj.Activity.Settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.inf2007_proj.Activity.Homepage.Homepage
import com.example.inf2007_proj.R
import com.example.inf2007_proj.Session.Session
import com.example.inf2007_proj.ViewModel.SettingsViewModel

class Settings : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var session: Session

    private lateinit var editTextSTSName: EditText
    private lateinit var editTextSTSContactNumber: EditText
    private lateinit var editTextSTSEmail: EditText
    private lateinit var editTextSTSHouseAddress: EditText
    private lateinit var editTextSTSPostalCode: EditText
    private lateinit var editTextSTSPassword: EditText
    private lateinit var editTextSTSConfirmPassword: EditText
    private lateinit var buttonSettingsEdit: Button
    private lateinit var buttonSettingsBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        session = Session(applicationContext)
        val username = session.getUsername()

        // Redirect to login if session is invalid
        if (username.isEmpty()) {
            startActivity(Intent(this, Homepage::class.java))
            finish()
            return
        }

        // Initialize UI Components
        editTextSTSName = findViewById(R.id.editTextSTSName)
        editTextSTSContactNumber = findViewById(R.id.editTextSTSContact)
        editTextSTSEmail = findViewById(R.id.editTextSTSEmail)
        editTextSTSHouseAddress = findViewById(R.id.editTextSTSHouseAddress)
        editTextSTSPostalCode = findViewById(R.id.editTextSTSPostalCode)
        editTextSTSPassword = findViewById(R.id.editTextSTSPassword)
        editTextSTSConfirmPassword = findViewById(R.id.editTextSTSConfirmPassword)
        buttonSettingsEdit = findViewById(R.id.buttonSettingsEdit)
        buttonSettingsBack = findViewById(R.id.buttonSettingsBack)

        // Observe Settings Data from ViewModel
        settingsViewModel.settingsData.observe(this) { settings ->
            if (settings != null) {
                editTextSTSName.setText(settings["Username"])
                editTextSTSContactNumber.setText(settings["ContactNum"])
                editTextSTSEmail.setText(settings["EmailAdd"])
                editTextSTSHouseAddress.setText(settings["HouseAdd"])
                editTextSTSPostalCode.setText(settings["HousePostal"])
                editTextSTSPassword.setText(settings["Password"])
                editTextSTSConfirmPassword.setText(settings["Password"])
                setEditTextsEnabled(false)
            }
        }

        // Observe successful update and redirect to homepage
        settingsViewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Homepage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_SHORT).show()
            }
        }

        // Load user settings
        settingsViewModel.loadSettings(username)

        // Button Click Listeners
        buttonSettingsEdit.setOnClickListener {
            if (buttonSettingsEdit.text == "Edit Profile Details") {
                setEditTextsEnabled(true)
                buttonSettingsEdit.text = "Save Profile Details"
                buttonSettingsBack.text = "Cancel"
            } else {
                val password = editTextSTSPassword.text.toString()
                val confirmPassword = editTextSTSConfirmPassword.text.toString()

                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                settingsViewModel.updateSettings(
                    username,
                    password,
                    editTextSTSContactNumber.text.toString(),
                    editTextSTSEmail.text.toString(),
                    editTextSTSHouseAddress.text.toString(),
                    editTextSTSPostalCode.text.toString()
                )
            }
        }

        buttonSettingsBack.setOnClickListener {
            if (buttonSettingsBack.text == "Cancel") {
                settingsViewModel.loadSettings(username)
                setEditTextsEnabled(false)
                buttonSettingsEdit.text = "Edit Profile Details"
                buttonSettingsBack.text = "Back to Home"
            } else {
                startActivity(Intent(this, Homepage::class.java))
            }
        }
    }

    private fun setEditTextsEnabled(enabled: Boolean) {
        editTextSTSName.isEnabled = enabled
        editTextSTSContactNumber.isEnabled = enabled
        editTextSTSEmail.isEnabled = enabled
        editTextSTSHouseAddress.isEnabled = enabled
        editTextSTSPostalCode.isEnabled = enabled
        editTextSTSPassword.isEnabled = enabled
        editTextSTSConfirmPassword.isEnabled = enabled
    }
}
