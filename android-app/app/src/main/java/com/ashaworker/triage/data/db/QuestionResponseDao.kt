package com.ashaworker.triage.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponses(responses: List<QuestionResponseEntity>)

    @Query("SELECT * FROM question_responses WHERE visitId = :visitId")
    suspend fun getResponsesForVisit(visitId: String): List<QuestionResponseEntity>
}
