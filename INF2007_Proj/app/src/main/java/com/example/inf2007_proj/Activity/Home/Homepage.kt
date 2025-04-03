package com.example.inf2007_proj.Activity.Homepage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.inf2007_proj.Activity.ScanPrescription.ScanPrescriptionActivity
import com.example.inf2007_proj.Activity.MedicationReminder.MedicationActivity
import com.example.inf2007_proj.Activity.PharmacyFinder.PharmacyFinderActivity
import com.example.inf2007_proj.Activity.Settings.Settings
import com.example.inf2007_proj.MainActivity
import com.example.inf2007_proj.R
import com.example.inf2007_proj.Session.Session

class Homepage : AppCompatActivity() {

    private lateinit var textViewWelcome: TextView
    private lateinit var buttonHomeReminders: Button
    private lateinit var buttonHomeNearbyPharmacy: Button
    private lateinit var buttonScanPrescription: Button
    private lateinit var buttonHomeSetting: Button
    private lateinit var buttonHomeLogout: Button
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize Session
        session = Session(applicationContext)

        // Check if the session is valid
        val username = session.getUsername()
        if (username.isEmpty()) {
            // If session is empty, redirect to login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close Homepage to prevent navigation back here
            return
        }

        // Find Views
        textViewWelcome = findViewById(R.id.textViewHomeName)
        buttonHomeReminders = findViewById(R.id.buttonHomeReminders)
        buttonHomeNearbyPharmacy = findViewById(R.id.buttonHomeNearbyPharmacy)
        buttonScanPrescription = findViewById(R.id.buttonScanPrescription)
        buttonHomeSetting = findViewById(R.id.buttonHomeSetting)
        buttonHomeLogout = findViewById(R.id.buttonHomeLogout)

        textViewWelcome.text = "Welcome back: $username"

        // Button Click Events
        buttonHomeReminders.setOnClickListener {
            val intent = Intent(this, MedicationActivity::class.java)
            startActivity(intent)
        }

        buttonHomeNearbyPharmacy.setOnClickListener {
            val intent = Intent(this, PharmacyFinderActivity::class.java)
            startActivity(intent)
        }

        buttonScanPrescription.setOnClickListener {
            val intent = Intent(this, ScanPrescriptionActivity::class.java)
            startActivity(intent)
        }

        buttonHomeSetting.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        buttonHomeLogout.setOnClickListener {
            session.setLogin(false) // Clear session
            session.setUsername("") // Clear username
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close Homepage to prevent navigation back here
        }
    }
}
