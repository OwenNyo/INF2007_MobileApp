package com.example.inf2007_proj.Activity.Registration

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.inf2007_proj.R
import com.example.inf2007_proj.ViewModel.RegistrationViewModel

class Registration : AppCompatActivity() {

    private val registrationViewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextContact = findViewById<EditText>(R.id.editTextContactNumber)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextAddress = findViewById<EditText>(R.id.editTextHouseAddress)
        val editTextPostal = findViewById<EditText>(R.id.editTextPostalCode)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val username = editTextName.text.toString()
            val contact = editTextContact.text.toString()
            val email = editTextEmail.text.toString()
            val address = editTextAddress.text.toString()
            val postal = editTextPostal.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrationViewModel.registerUser(username, password, contact, email, address, postal)
        }

        registrationViewModel.registrationStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

