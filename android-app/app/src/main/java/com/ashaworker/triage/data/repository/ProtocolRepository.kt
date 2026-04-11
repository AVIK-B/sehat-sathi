package com.ashaworker.triage.data.repository

import com.ashaworker.triage.data.model.DangerSignsEnvelope
import com.ashaworker.triage.data.model.Protocol
import com.ashaworker.triage.data.protocol.ProtocolLoader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolRepository @Inject constructor(
    private val protocolLoader: ProtocolLoader
) {

    fun loadProtocol(patientType: String, chiefComplaint: String): Flow<Protocol> = flow {
        // TODO: Validate each protocol JSON and branching logic with state IMNCI trainers before production rollout.
        val file = when (patientType.uppercase()) {
            "CHILD" -> when (chiefComplaint.lowercase()) {
                "fever" -> "child_fever.json"
                "diarrhea" -> "child_diarrhea.json"
                else -> "child_breathing.json"
            }
            "NEWBORN" -> "newborn_danger.json"
            "PREGNANT" -> "pregnant_danger.json"
            "ADULT" -> "adult_general.json"
            else -> "child_fever.json"
        }
        emit(protocolLoader.loadProtocol(file))
    }

    fun loadDangerSigns(): DangerSignsEnvelope = protocolLoader.loadDangerSigns()
}
