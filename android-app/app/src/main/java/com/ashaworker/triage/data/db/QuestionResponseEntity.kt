package com.ashaworker.triage.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "question_responses",
    foreignKeys = [
        ForeignKey(
            entity = VisitEntity::class,
            parentColumns = ["id"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("visitId")]
)
data class QuestionResponseEntity(
    @PrimaryKey val id: String,
    val visitId: String,
    val questionId: String,
    val questionText: String,
    val response: String
)
