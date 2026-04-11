package com.ashaworker.triage.ui.triage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashaworker.triage.data.db.QuestionResponseEntity
import com.ashaworker.triage.data.db.VisitEntity
import com.ashaworker.triage.data.model.Protocol
import com.ashaworker.triage.data.model.TriageResult
import com.ashaworker.triage.data.model.TriageStep
import com.ashaworker.triage.data.repository.AuthRepository
import com.ashaworker.triage.data.repository.ProtocolRepository
import com.ashaworker.triage.data.repository.VisitRepository
import com.ashaworker.triage.util.LocationHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TriageViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val visitRepository: VisitRepository,
    private val locationHelper: LocationHelper,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var currentProtocol: Protocol? = null

    private val responses = linkedMapOf<String, Pair<String, String>>()

    private val _resultState = MutableStateFlow<TriageResult?>(null)
    val resultState: StateFlow<TriageResult?> = _resultState.asStateFlow()

    private val _currentQuestionId = MutableStateFlow<String?>(null)
    val currentQuestionId: StateFlow<String?> = _currentQuestionId.asStateFlow()

    private val _protocolTitle = MutableStateFlow("")
    val protocolTitle: StateFlow<String> = _protocolTitle.asStateFlow()

    private val _flowState = MutableStateFlow(TriageFlowState())
    val flowState: StateFlow<TriageFlowState> = _flowState.asStateFlow()

    fun loadProtocol(patientType: String, chiefComplaint: String): Flow<Protocol> {
        _flowState.update {
            it.copy(patientType = patientType, chiefComplaint = chiefComplaint)
        }
        return protocolRepository.loadProtocol(patientType, chiefComplaint)
    }

    fun setProtocol(protocol: Protocol) {
        currentProtocol = protocol
        _protocolTitle.value = protocol.title
        _currentQuestionId.value = protocol.questions.firstOrNull()?.id
    }

    fun setCurrentQuestion(questionId: String) {
        _currentQuestionId.value = questionId
    }

    fun getQuestion(questionId: String) = currentProtocol?.questions?.firstOrNull { it.id == questionId }

    fun getTotalQuestionCount(): Int = currentProtocol?.questions?.size ?: 0

    fun getCurrentIndex(questionId: String): Int {
        val index = currentProtocol?.questions?.indexOfFirst { it.id == questionId } ?: -1
        return if (index < 0) 0 else index + 1
    }

    fun getNextStep(currentQuestionId: String, response: String): TriageStep {
        val protocol = currentProtocol ?: throw IllegalStateException("Protocol not loaded")
        val question = protocol.questions.firstOrNull { it.id == currentQuestionId }
            ?: throw IllegalStateException("Question not found")

        val normalizedResponse = response.uppercase()
        val rawNext = question.next[response] ?: question.next[normalizedResponse]
            ?: throw IllegalStateException("No next step configured")

        return if (rawNext.startsWith("RESULT_")) {
            val result = protocol.results[rawNext]
                ?: throw IllegalStateException("Result not found")
            val referralRequired = result.classification != "GREEN"
            val urgency = when (result.classification) {
                "RED" -> "IMMEDIATE"
                "YELLOW" -> "WITHIN_24H"
                else -> "NONE"
            }
            val triageResult = TriageResult(
                classification = result.classification,
                title = result.title,
                hindi = result.hindi,
                advice = result.advice,
                medicines = result.medicines,
                referralRequired = referralRequired,
                referralUrgency = urgency
            )
            _resultState.value = triageResult
            TriageStep.Result(rawNext, result)
        } else {
            _currentQuestionId.value = rawNext
            TriageStep.NextQuestion(rawNext)
        }
    }

    fun recordResponse(questionId: String, response: String) {
        val questionText = getQuestion(questionId)?.text ?: questionId
        responses[questionId] = questionText to response
    }

    fun setImmediateResult(result: TriageResult) {
        _resultState.value = result
    }

    suspend fun saveVisit(patientType: String, result: TriageResult): Result<String> {
        return runCatching {
            val workerId = authRepository.getWorkerId()
            val location = locationHelper.getLastKnownLocationOrNull()
            val visitId = UUID.randomUUID().toString()
            val adviceJson = Gson().toJson(result.advice)

            val visit = VisitEntity(
                id = visitId,
                workerId = workerId,
                patientType = patientType,
                patientAgeMonths = null,
                chiefComplaint = flowState.value.chiefComplaint,
                triageResult = result.classification,
                adviceGiven = adviceJson,
                referralRequired = result.referralRequired,
                referralUrgency = result.referralUrgency,
                latitude = location?.latitude,
                longitude = location?.longitude,
                visitTimestamp = System.currentTimeMillis(),
                synced = false,
                syncAttempts = 0
            )
            visitRepository.insertVisit(visit)

            val responseEntities = responses.map { (questionId, data) ->
                QuestionResponseEntity(
                    id = UUID.randomUUID().toString(),
                    visitId = visitId,
                    questionId = questionId,
                    questionText = data.first,
                    response = data.second
                )
            }
            if (responseEntities.isNotEmpty()) {
                visitRepository.insertResponses(responseEntities)
            }
            visitId
        }
    }

    fun resetSession() {
        responses.clear()
        _currentQuestionId.value = null
        _resultState.value = null
        _flowState.value = TriageFlowState()
    }
}

data class TriageFlowState(
    val patientType: String = "",
    val chiefComplaint: String = "fever"
)
