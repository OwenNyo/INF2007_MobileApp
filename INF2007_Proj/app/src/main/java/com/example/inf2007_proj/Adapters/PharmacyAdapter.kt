package com.example.inf2007_proj.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inf2007_proj.DataModels.Pharmacy
import com.example.inf2007_proj.R

class PharmacyAdapter : ListAdapter<Pharmacy, PharmacyAdapter.PharmacyViewHolder>(PharmacyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pharmacy_recycleview, parent, false)
        return PharmacyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {
        val pharmacy = getItem(position)
        holder.bind(pharmacy)
    }

    class PharmacyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textPharmacyName)
        private val addressTextView: TextView = itemView.findViewById(R.id.textPharmacyAddress)
        private val distanceTextView: TextView = itemView.findViewById(R.id.textDistance)
        private val viewMapButton: Button = itemView.findViewById(R.id.buttonViewInMap) // Updated ID

        fun bind(pharmacy: Pharmacy) {
            nameTextView.text = pharmacy.name
            addressTextView.text = pharmacy.address
            distanceTextView.text = pharmacy.distance

            // Set the OnClickListener for the "View in Map" button
            viewMapButton.setOnClickListener {
                // Create a URI to open Google Maps with the pharmacy's address
                val uri = "geo:0,0?q=${pharmacy.address}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                itemView.context.startActivity(intent)
            }
        }
    }

    class PharmacyDiffCallback : DiffUtil.ItemCallback<Pharmacy>() {
        override fun areItemsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
            return oldItem.name == newItem.name // Assuming names are unique
        }

        override fun areContentsTheSame(oldItem: Pharmacy, newItem: Pharmacy): Boolean {
            return oldItem == newItem // Uses data class default equals() check
        }
    }
}
