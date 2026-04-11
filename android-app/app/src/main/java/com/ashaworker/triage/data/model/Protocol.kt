package com.ashaworker.triage.data.model

data class DangerSignsEnvelope(
    val signs: List<DangerSign>
)

data class DangerSign(
    val id: String,
    val text: String,
    val hindi: String,
    val icon: String? = null
)

data class Protocol(
    val protocolId: String,
    val patientType: String,
    val chiefComplaint: String,
    val title: String,
    val questions: List<ProtocolQuestion>,
    val results: Map<String, ProtocolResult>
)

data class ProtocolQuestion(
    val id: String,
    val text: String,
    val hindi: String,
    val type: String,
    val choices: List<String>? = null,
    val next: Map<String, String>
)

data class ProtocolResult(
    val classification: String,
    val title: String,
    val hindi: String,
    val reason: String? = null,
    val advice: List<String>,
    val medicines: List<String>
)

enum class PatientType {
    NEWBORN,
    CHILD,
    PREGNANT,
    ADULT
}
