package com.ashaworker.triage.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ashaworker.triage.R
import com.ashaworker.triage.databinding.ResultFragmentBinding
import com.ashaworker.triage.ui.triage.TriageViewModel
import kotlinx.coroutines.launch

class ResultFragment : Fragment() {

    private var _binding: ResultFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TriageViewModel by activityViewModels()

    private val args by lazy { ResultFragmentArgs.fromBundle(requireArguments()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResultFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = viewModel.resultState.value ?: return
        when (result.classification) {
            "RED" -> {
                binding.header.setBackgroundColor(requireContext().getColor(R.color.red_emergency))
                binding.tvClassification.setText(R.string.result_red)
            }
            "YELLOW" -> {
                binding.header.setBackgroundColor(requireContext().getColor(R.color.yellow_referral))
                binding.tvClassification.setText(R.string.result_yellow)
            }
            else -> {
                binding.header.setBackgroundColor(requireContext().getColor(R.color.green_home))
                binding.tvClassification.setText(R.string.result_green)
            }
        }

        binding.tvClassificationHindi.text = result.hindi
        binding.tvAdvice.text = result.advice.mapIndexed { index, s -> "${index + 1}. $s" }.joinToString("\n")
        binding.tvMedicines.text = if (result.medicines.isEmpty()) "-" else result.medicines.joinToString("\n")
        binding.tvWhereToGo.text = when (result.referralUrgency) {
            "IMMEDIATE" -> "Nearest PHC or CHC now"
            "WITHIN_24H" -> "Visit PHC within 24 hours"
            else -> "Home follow-up"
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                val save = viewModel.saveVisit(args.patientType, result)
                save.onSuccess { id ->
                    Toast.makeText(requireContext(), "Saved $id. ${getString(R.string.gps_saved)}", Toast.LENGTH_LONG).show()
                    shareSms(result.classification)
                    viewModel.resetSession()
                    findNavController().navigate(ResultFragmentDirections.actionResultToHome())
                }.onFailure {
                    Toast.makeText(requireContext(), "Save failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareSms(result: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:"))
        intent.putExtra("sms_body", "Triage outcome: $result")
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
