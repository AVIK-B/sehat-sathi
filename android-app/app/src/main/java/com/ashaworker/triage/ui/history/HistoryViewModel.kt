package com.ashaworker.triage.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashaworker.triage.data.db.VisitEntity
import com.ashaworker.triage.data.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    visitRepository: VisitRepository
) : ViewModel() {

    private val search = MutableStateFlow("")

    val visits: StateFlow<List<VisitEntity>> = combine(
        visitRepository.getAllVisits(),
        search
    ) { list, q ->
        val query = q.trim().lowercase()
        if (query.isBlank()) list else list.filter {
            it.triageResult.lowercase().contains(query) ||
                it.patientType.lowercase().contains(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearch(query: String) {
        search.value = query
    }
}
