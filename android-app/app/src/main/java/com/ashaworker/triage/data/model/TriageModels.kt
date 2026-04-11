package com.ashaworker.triage.data.model

sealed class TriageStep {
    data class NextQuestion(val id: String) : TriageStep()
    data class Result(val resultId: String, val payload: ProtocolResult) : TriageStep()
}

data class TriageResult(
    val classification: String,
    val title: String,
    val hindi: String,
    val advice: List<String>,
    val medicines: List<String>,
    val referralRequired: Boolean,
    val referralUrgency: String
)

data class VisitApiModel(
    val id: String,
    val workerId: String,
    val patientType: String,
    val patientAgeMonths: Int?,
    val chiefComplaint: String,
    val triageResult: String,
    val adviceGiven: List<String>,
    val referralRequired: Boolean,
    val referralUrgency: String,
    val latitude: Double?,
    val longitude: Double?,
    val visitTimestamp: Long,
    val questionResponses: List<QuestionResponseApiModel>
)

data class QuestionResponseApiModel(
    val questionId: String,
    val questionText: String,
    val response: String
)

data class LoginRequest(val workerId: String, val pin: String)

data class LoginResponse(
    val token: String,
    val workerId: String,
    val name: String,
    val block: String,
    val district: String,
    val role: String
)

data class ProtocolVersionResponse(
    val versions: Map<String, String>
)
