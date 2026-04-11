package com.ashaworker.triage.ui.triage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ashaworker.triage.data.model.TriageStep
import com.ashaworker.triage.databinding.TriageFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TriageFragment : Fragment() {

    private var _binding: TriageFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TriageViewModel by activityViewModels()

    private val args by lazy { TriageFragmentArgs.fromBundle(requireArguments()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TriageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val protocol = viewModel.loadProtocol(args.patientType, args.chiefComplaint).first()
            viewModel.setProtocol(protocol)
            val initialQuestion = args.questionId.ifBlank {
                viewModel.currentQuestionId.value.orEmpty()
            }
            if (initialQuestion.isNotBlank()) {
                viewModel.setCurrentQuestion(initialQuestion)
            }
            renderCurrentQuestion()
        }

        binding.btnYes.setOnClickListener { handleResponse("YES") }
        binding.btnNo.setOnClickListener { handleResponse("NO") }
        binding.tvBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun renderCurrentQuestion() {
        val questionId = viewModel.currentQuestionId.value ?: return
        val question = viewModel.getQuestion(questionId) ?: return

        binding.tvProgress.text = "Step ${viewModel.getCurrentIndex(questionId)} of ${viewModel.getTotalQuestionCount()}"
        binding.tvCondition.text = viewModel.protocolTitle.value
        binding.tvQuestion.text = question.text
        binding.tvQuestionHindi.text = question.hindi

        if (question.type == "YES_NO") {
            binding.yesNoContainer.visibility = View.VISIBLE
            binding.choiceGroup.visibility = View.GONE
        } else {
            binding.yesNoContainer.visibility = View.GONE
            binding.choiceGroup.visibility = View.VISIBLE
            binding.choiceGroup.removeAllViews()
            question.choices.orEmpty().forEach { choice ->
                val radioButton = RadioButton(requireContext())
                radioButton.text = choice
                radioButton.textSize = 20f
                radioButton.minHeight = 80
                radioButton.setOnClickListener { handleResponse(choice) }
                binding.choiceGroup.addView(radioButton)
            }
        }
    }

    private fun handleResponse(response: String) {
        val questionId = viewModel.currentQuestionId.value ?: return
        viewModel.recordResponse(questionId, response)
        when (val step = viewModel.getNextStep(questionId, response)) {
            is TriageStep.NextQuestion -> {
                viewModel.setCurrentQuestion(step.id)
                renderCurrentQuestion()
            }
            is TriageStep.Result -> {
                val action = TriageFragmentDirections.actionTriageToResult(args.patientType)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
