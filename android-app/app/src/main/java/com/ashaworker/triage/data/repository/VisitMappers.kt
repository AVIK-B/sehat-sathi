package com.ashaworker.triage.data.repository

import com.ashaworker.triage.data.db.QuestionResponseEntity
import com.ashaworker.triage.data.db.VisitEntity
import com.ashaworker.triage.data.model.QuestionResponseApiModel
import com.ashaworker.triage.data.model.VisitApiModel
import com.google.gson.Gson

private val gson = Gson()

fun VisitEntity.toApiModel(responses: List<QuestionResponseEntity>): VisitApiModel {
    val advice = runCatching {
        gson.fromJson(adviceGiven, Array<String>::class.java).toList()
    }.getOrDefault(emptyList())

    return VisitApiModel(
        id = id,
        workerId = workerId,
        patientType = patientType,
        patientAgeMonths = patientAgeMonths,
        chiefComplaint = chiefComplaint,
        triageResult = triageResult,
        adviceGiven = advice,
        referralRequired = referralRequired,
        referralUrgency = referralUrgency,
        latitude = latitude,
        longitude = longitude,
        visitTimestamp = visitTimestamp,
        questionResponses = responses.map {
            QuestionResponseApiModel(
                questionId = it.questionId,
                questionText = it.questionText,
                response = it.response
            )
        }
    )
}
