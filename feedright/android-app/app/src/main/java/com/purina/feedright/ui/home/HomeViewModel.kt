package com.purina.feedright.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purina.feedright.data.local.entity.VisitEntity
import com.purina.feedright.data.repository.AuthRepository
import com.purina.feedright.data.repository.FarmRepository
import com.purina.feedright.data.repository.ProductRepository
import com.purina.feedright.data.repository.VisitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val visitRepository: VisitRepository,
    private val farmRepository: FarmRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        syncIfNeeded()
    }

    private fun loadData() {
        // Combine visits, farms, and products into a single UI state
        viewModelScope.launch {
            combine(
                visitRepository.getRecentVisits(),
                farmRepository.getAllFarms(),
                productRepository.getActiveProducts()
            ) { visits, farms, products ->
                HomeUiState(
                    visits = visits,
                    salesmanName = authRepository.getSalesmanName() ?: "Unknown",
                    isLoading = false,
                    unsyncedCount = 0 // Will be updated by syncStatus
                )
            }.collect { state ->
                _uiState.value = state
                updateSyncStatus()
            }
        }
    }

    private fun syncIfNeeded() {
        viewModelScope.launch {
            // Sync farms and products if needed
            if (farmRepository.needsSync()) {
                farmRepository.syncFarms()
            }
            if (productRepository.needsSync()) {
                productRepository.syncProducts()
            }
        }
    }

    private fun updateSyncStatus() {
        viewModelScope.launch {
            val unsyncedCount = visitRepository.getUnsyncedCount()
            _uiState.value = _uiState.value.copy(unsyncedCount = unsyncedCount)
        }
    }

    fun onSyncClicked() {
        _uiState.value = _uiState.value.copy(isSyncing = true, syncError = null)

        viewModelScope.launch {
            visitRepository.syncVisits()
                .onSuccess { syncedCount ->
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        syncMessage = if (syncedCount > 0) {
                            "Synced $syncedCount visit(s)"
                        } else {
                            "All visits already synced"
                        }
                    )
                    updateSyncStatus()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        syncError = "Sync failed: ${error.message}"
                    )
                }
        }
    }

    fun onRefresh() {
        loadData()
        syncIfNeeded()
    }
}

data class HomeUiState(
    val visits: List<VisitEntity> = emptyList(),
    val salesmanName: String = "",
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val unsyncedCount: Int = 0,
    val syncMessage: String? = null,
    val syncError: String? = null
)
