package com.ashaworker.triage.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey val id: String,
    val workerId: String,
    val patientType: String,
    val patientAgeMonths: Int?,
    val chiefComplaint: String,
    val triageResult: String,
    val adviceGiven: String,
    val referralRequired: Boolean,
    val referralUrgency: String,
    val latitude: Double?,
    val longitude: Double?,
    val visitTimestamp: Long,
    val synced: Boolean = false,
    val syncAttempts: Int = 0
)
