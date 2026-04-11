package com.ashaworker.triage.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ashaworker.triage.data.repository.ApiService
import com.ashaworker.triage.data.repository.VisitRepository
import com.ashaworker.triage.data.repository.toApiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val visitRepository: VisitRepository,
    private val apiService: ApiService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val unsynced = visitRepository.getUnsyncedVisits()
        if (unsynced.isEmpty()) return Result.success()

        return try {
            unsynced.chunked(20).forEach { batch ->
                val payload = batch.map { visit ->
                    val responses = visitRepository.getResponsesForVisit(visit.id)
                    visit.toApiModel(responses)
                }
                val response = apiService.uploadVisits(payload)
                if (response.isSuccessful) {
                    visitRepository.markAsSynced(batch.map { it.id })
                } else {
                    visitRepository.incrementSyncAttempts(batch.map { it.id })
                }
            }
            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val PERIODIC_SYNC_TAG = "periodic_sync"
    }
}
