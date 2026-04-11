package com.ashaworker.triage.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ashaworker.triage.R
import com.ashaworker.triage.data.repository.AuthRepository
import com.ashaworker.triage.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvWorkerName.text = authRepository.getWorkerName()

        binding.btnNewborn.setOnClickListener { goToDangerSigns("NEWBORN", "newborn") }
        binding.btnPregnant.setOnClickListener { goToDangerSigns("PREGNANT", "pregnancy") }
        binding.btnAdult.setOnClickListener { goToDangerSigns("ADULT", "fever") }
        binding.btnChild.setOnClickListener { showChildComplaintPicker() }

        binding.tvHistory.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_history)
        }
        binding.tvSyncBadge.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_sync)
        }
    }

    private fun showChildComplaintPicker() {
        val complaints = arrayOf("fever", "diarrhea", "breathing")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.chief_complaint_prompt)
            .setItems(complaints) { _, which ->
                goToDangerSigns("CHILD", complaints[which])
            }
            .show()
    }

    private fun goToDangerSigns(patientType: String, chiefComplaint: String) {
        val action = HomeFragmentDirections.actionHomeToDanger(patientType, chiefComplaint)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
