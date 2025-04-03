package com.example.inf2007_proj.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inf2007_proj.Adapters.MedicationAdapter
import com.example.inf2007_proj.DataModels.MedicationEntity
import com.example.inf2007_proj.MainActivity
import com.example.inf2007_proj.R
import com.example.inf2007_proj.Session.Session
import com.example.inf2007_proj.Utils.NotificationUtils
import com.example.inf2007_proj.ViewModel.MedicationViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MedicationListFragment : Fragment(), MedicationDialogFragment.MedicationDialogListener {

    private lateinit var medicationAdapter: MedicationAdapter
    private val viewModel: MedicationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_medication_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = Session(requireContext())
        val username = session.getUsername()

        if (username.isEmpty()) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
            return
        }

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        medicationAdapter = MedicationAdapter(
            emptyList(),
            onEditClick = { medication ->
                val dialog = MedicationDialogFragment.newInstance(medication)
                dialog.show(childFragmentManager, "MedicationDialog")
            }
        )
        recyclerView.adapter = medicationAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Observe LiveData from ViewModel
        viewModel.medications.observe(viewLifecycleOwner, Observer { medications ->
            medicationAdapter.updateData(medications)
            NotificationUtils.scheduleDailyNotifications(requireContext(), medications)
        })

        // Load medications
        viewModel.loadMedications(username)

        // Setup Floating Action Button
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_medication)
        fab.setOnClickListener {
            val dialog = MedicationDialogFragment.newInstance(isLocalMode = false)
            dialog.show(childFragmentManager, "MedicationDialog")
        }
    }

    override fun onMedicationUpdatedOrAdded() {
        val session = Session(requireContext())
        val username = session.getUsername()
        viewModel.loadMedications(username)
    }
}
