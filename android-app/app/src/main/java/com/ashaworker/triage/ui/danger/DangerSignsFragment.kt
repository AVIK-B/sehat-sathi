package com.ashaworker.triage.ui.danger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashaworker.triage.R
import com.ashaworker.triage.data.model.TriageResult
import com.ashaworker.triage.data.repository.ProtocolRepository
import com.ashaworker.triage.databinding.DangerSignsFragmentBinding
import com.ashaworker.triage.ui.triage.TriageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DangerSignsFragment : Fragment() {

    private var _binding: DangerSignsFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var protocolRepository: ProtocolRepository

    private val triageViewModel: TriageViewModel by activityViewModels()
    private lateinit var adapter: DangerSignsAdapter

    private val args by lazy { DangerSignsFragmentArgs.fromBundle(requireArguments()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DangerSignsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DangerSignsAdapter { _, _ -> updateDangerButtonVisuals() }
        binding.rvDangerSigns.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDangerSigns.adapter = adapter

        val signs = protocolRepository.loadDangerSigns().signs
        adapter.submitList(signs)

        binding.btnDanger.setOnClickListener {
            if (adapter.hasAnyChecked()) {
                triageViewModel.setImmediateResult(
                    TriageResult(
                        classification = "RED",
                        title = getString(R.string.result_red),
                        hindi = getString(R.string.result_red),
                        advice = listOf(
                            "Do not delay referral",
                            "Keep patient warm and airway clear",
                            "Go to nearest PHC/CHC now"
                        ),
                        medicines = emptyList(),
                        referralRequired = true,
                        referralUrgency = "IMMEDIATE"
                    )
                )
                val action = DangerSignsFragmentDirections.actionDangerToResult(args.patientType)
                findNavController().navigate(action)
            }
        }

        binding.btnContinue.setOnClickListener {
            val action = DangerSignsFragmentDirections.actionDangerToTriage(
                patientType = args.patientType,
                chiefComplaint = args.chiefComplaint,
                questionId = ""
            )
            findNavController().navigate(action)
        }

        updateDangerButtonVisuals()
    }

    private fun updateDangerButtonVisuals() {
        if (adapter.hasAnyChecked()) {
            val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
            binding.btnDanger.startAnimation(pulse)
        } else {
            binding.btnDanger.clearAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
