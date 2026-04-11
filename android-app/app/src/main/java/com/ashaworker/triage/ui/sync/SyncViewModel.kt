package com.ashaworker.triage.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ashaworker.triage.data.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val visitRepository: VisitRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _unsyncedCount = MutableStateFlow(0)
    val unsyncedCount: StateFlow<Int> = _unsyncedCount.asStateFlow()

    private val _lastSyncLabel = MutableStateFlow("Never")
    val lastSyncLabel: StateFlow<String> = _lastSyncLabel.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _syncing = MutableStateFlow(false)
    val syncing: StateFlow<Boolean> = _syncing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _unsyncedCount.value = visitRepository.getUnsyncedVisits().size
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _syncing.value = true
            val request = OneTimeWorkRequestBuilder<com.ashaworker.triage.worker.SyncWorker>().build()
            workManager.enqueue(request)
            val done = workManager.getWorkInfoByIdFlow(request.id).first {
                it.state == WorkInfo.State.SUCCEEDED ||
                    it.state == WorkInfo.State.FAILED ||
                    it.state == WorkInfo.State.CANCELLED
            }
            _syncing.value = false
            _lastSyncLabel.value = Date().toString()
            val status = if (done.state == WorkInfo.State.SUCCEEDED) "Sync success" else "Sync failed"
            _logs.value = listOf("${Date()}: $status") + _logs.value
            refresh()
        }
    }
}
