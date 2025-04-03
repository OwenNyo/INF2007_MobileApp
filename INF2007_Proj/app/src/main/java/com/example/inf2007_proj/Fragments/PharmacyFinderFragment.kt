package com.example.inf2007_proj.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inf2007_proj.Adapters.PharmacyAdapter
import com.example.inf2007_proj.DataModels.Pharmacy
import com.example.inf2007_proj.R
import com.example.inf2007_proj.ViewModel.PharmacyFinderViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PharmacyFinderFragment : Fragment() {

    private val pharmacyFinderViewModel: PharmacyFinderViewModel by viewModels()
    private lateinit var pharmacyAdapter: PharmacyAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var buttonSearchCurrentLocation: Button
    private var lastLocation: Location? = null // Stores last known location to prevent unnecessary API calls
    private val locationThreshold = 50 // Minimum distance change (in meters) before fetching new data
    private var locationJob: Job? = null // Job for handling async location fetch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pharmacy_finder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        buttonSearchCurrentLocation = view.findViewById(R.id.btnSearchCurrentLocation)

        // Setup RecyclerView
        val recyclerViewPharmacies = view.findViewById<RecyclerView>(R.id.recyclerViewPharmacies)
        pharmacyAdapter = PharmacyAdapter()
        recyclerViewPharmacies.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPharmacies.setHasFixedSize(true) // Optimizes layout recalculations
        recyclerViewPharmacies.adapter = pharmacyAdapter

        // Observe ViewModel LiveData and update list using `submitList()`
        pharmacyFinderViewModel.pharmacies.observe(viewLifecycleOwner) { pharmacies ->
            Log.d("LiveDataUpdate", "Updating pharmacies list: ${pharmacies.size} items found.")
            pharmacyAdapter.submitList(pharmacies) // Efficient updates
        }

        // Handle Button Click for Location Search
        buttonSearchCurrentLocation.setOnClickListener {
            requestLocationAndFetchPharmacies()
        }
    }

    private fun requestLocationAndFetchPharmacies() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Cancel any previous location job to prevent redundant calls
        locationJob?.cancel()
        locationJob = CoroutineScope(Dispatchers.IO).launch {
            val location = getLastKnownLocation() // Suspend function to get location safely
            if (location != null) {
                if (shouldFetchNewData(location)) {
                    lastLocation = location
                    //pharmacyFinderViewModel.fetchNearbyPharmacies(requireContext())
                    Log.d("Phone Location", "Received: ${location.latitude.toString() + " " + location.longitude.toString()}")
                    pharmacyFinderViewModel.fetchNearbyPharmacies(requireContext(), location.latitude, location.longitude)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getLastKnownLocation(): Location? {
        return if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, return null
            null
        } else {
            // Safe coroutine call to fetch location
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener { _ ->
                    continuation.resume(null) // Return null if location fetch fails
                }
            }
        }
    }


    private fun shouldFetchNewData(newLocation: Location): Boolean {
        return lastLocation == null || lastLocation!!.distanceTo(newLocation) > locationThreshold
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                requestLocationAndFetchPharmacies()
            } else {
                Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
