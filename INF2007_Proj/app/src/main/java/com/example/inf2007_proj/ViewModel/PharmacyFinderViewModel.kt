package com.example.inf2007_proj.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.inf2007_proj.DataModels.Pharmacy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException

class PharmacyFinderViewModel : ViewModel() {

    private val _pharmacies = MutableLiveData<List<Pharmacy>>()
    val pharmacies: LiveData<List<Pharmacy>> get() = _pharmacies

    // 🔹 INLINE API KEY (Replace with your actual API key)
    private val apiKey = "INSERT KEY"

//    fun fetchNearbyPharmacies(context: Context) {
//        viewModelScope.launch(Dispatchers.IO) { // Use IO thread for network requests
//            // Coordinates for the location provided
//            val latitude = 1.4136
//            val longitude = 103.9123
//            val radius = 5000 // Radius in meters
//
//            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
//                    "location=$latitude,$longitude&radius=$radius&key=$apiKey"
//
//            val requestQueue = Volley.newRequestQueue(context)
//            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//                { response ->
//                    viewModelScope.launch(Dispatchers.IO) { // Parse JSON in IO thread
//                        try {
//                            Log.d("APIResponse", "Received: ${response.toString()}") // Log raw response
//                            val results = response.getJSONArray("results")
//                            val pharmacyList = mutableListOf<Pharmacy>()
//                            for (i in 0 until results.length()) {
//                                val place = results.getJSONObject(i)
//                                val name = place.getString("name")
//                                val address = place.getString("vicinity")
//
//                                pharmacyList.add(Pharmacy(name, address, "1 km"))
//                            }
//
//                            if (_pharmacies.value != pharmacyList) {
//                                withContext(Dispatchers.Main) {
//                                    _pharmacies.value = pharmacyList // Update LiveData on main thread
//                                }
//                            }
//                        } catch (e: JSONException) {
//                            Log.e("JSONError", "Error parsing JSON", e)
//                            e.printStackTrace()
//                        }
//                    }
//                },
//                { error ->
//                    val statusCode = error.networkResponse?.statusCode
//                    val errorMessage = error.networkResponse?.data?.let { String(it) }
//                    Log.e("API_ERROR", "Error: $statusCode, $errorMessage")
//                    error.printStackTrace()
//                }
//            )
//
//            requestQueue.add(jsonObjectRequest)
//        }
//    }

    fun fetchNearbyPharmacies(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) { // Use IO thread for network requests
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=$latitude,$longitude&radius=2000&type=pharmacy&key=$apiKey"

            val requestQueue = Volley.newRequestQueue(context)
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    viewModelScope.launch(Dispatchers.IO) { // Parse JSON in IO thread
                        try {
                            Log.d("APIResponse", "Received: ${response.toString()}") // Log raw response
                            val results = response.getJSONArray("results")
                            val pharmacyList = mutableListOf<Pharmacy>()
                            for (i in 0 until results.length()) {
                                val place = results.getJSONObject(i)
                                val name = place.getString("name")
                                val address = place.getString("vicinity")

                                pharmacyList.add(Pharmacy(name, address, "1 km"))
                            }

                            // Compare and update only if the data has changed
                            if (_pharmacies.value != pharmacyList) {
                                withContext(Dispatchers.Main) {
                                    _pharmacies.value = pharmacyList // Update LiveData on main thread
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Error parsing JSON", e)
                            e.printStackTrace()
                        }
                    }
                },
                { error ->
                    // Print error details
                    val statusCode = error.networkResponse?.statusCode
                    val errorMessage = error.networkResponse?.data?.let { String(it) }
                    Log.e("API_ERROR", "Error: $statusCode, $errorMessage")
                    error.printStackTrace()
                }
            )

            requestQueue.add(jsonObjectRequest)
        }
    }
}