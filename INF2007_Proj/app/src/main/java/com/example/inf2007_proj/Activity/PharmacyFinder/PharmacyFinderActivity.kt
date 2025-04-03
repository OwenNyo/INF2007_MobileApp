package com.example.inf2007_proj.Activity.PharmacyFinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inf2007_proj.Fragments.PharmacyFinderFragment
import com.example.inf2007_proj.R

class PharmacyFinderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy_finder)

        // Load the PharmacyFinderFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PharmacyFinderFragment())
                .commit()
        }
    }
}
