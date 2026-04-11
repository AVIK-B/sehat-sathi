package com.ashaworker.triage.data.repository

import com.ashaworker.triage.data.db.QuestionResponseDao
import com.ashaworker.triage.data.db.QuestionResponseEntity
import com.ashaworker.triage.data.db.VisitDao
import com.ashaworker.triage.data.db.VisitEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepository @Inject constructor(
    private val visitDao: VisitDao,
    private val questionResponseDao: QuestionResponseDao
) {

    suspend fun insertVisit(visit: VisitEntity) = visitDao.insertVisit(visit)

    suspend fun updateVisit(visit: VisitEntity) = visitDao.updateVisit(visit)

    suspend fun getVisitById(id: String) = visitDao.getVisitById(id)

    fun getAllVisits(): Flow<List<VisitEntity>> = visitDao.getAllVisits()

    suspend fun getUnsyncedVisits(): List<VisitEntity> = visitDao.getUnsyncedVisits()

    fun getAllVisitsForWorker(workerId: String): Flow<List<VisitEntity>> =
        visitDao.getAllVisitsForWorker(workerId)

    suspend fun getVisitCountToday(workerId: String): Int {
        return visitDao.getVisitCountToday(workerId)
    }

    suspend fun markAsSynced(ids: List<String>) = visitDao.markAsSynced(ids)

    suspend fun incrementSyncAttempts(ids: List<String>) = visitDao.incrementSyncAttempts(ids)

    suspend fun insertResponses(responses: List<QuestionResponseEntity>) =
        questionResponseDao.insertResponses(responses)

    suspend fun getResponsesForVisit(visitId: String) = questionResponseDao.getResponsesForVisit(visitId)
}
