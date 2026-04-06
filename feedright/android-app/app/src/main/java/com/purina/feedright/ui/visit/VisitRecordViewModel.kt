package com.purina.feedright.ui.visit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purina.feedright.data.local.entity.FarmEntity
import com.purina.feedright.data.local.entity.ProductEntity
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
 * ViewModel for Visit Recording screen
 */
@HiltViewModel
class VisitRecordViewModel @Inject constructor(
    private val visitRepository: VisitRepository,
    private val farmRepository: FarmRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitRecordUiState())
    val uiState: StateFlow<VisitRecordUiState> = _uiState.asStateFlow()

    init {
        loadFarmsAndProducts()
    }

    private fun loadFarmsAndProducts() {
        viewModelScope.launch {
            combine(
                farmRepository.getAllFarms(),
                productRepository.getActiveProducts()
            ) { farms, products ->
                VisitRecordUiState(
                    farms = farms,
                    products = products,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onFarmSelected(farm: FarmEntity?) {
        _uiState.value = _uiState.value.copy(
            selectedFarm = farm,
            errorMessage = null
        )
    }

    fun onProductSelected(product: ProductEntity?) {
        _uiState.value = _uiState.value.copy(
            selectedProduct = product,
            errorMessage = null
        )
    }

    fun onQuantityChanged(quantity: String) {
        _uiState.value = _uiState.value.copy(
            quantity = quantity,
            errorMessage = null
        )
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(
            notes = notes
        )
    }

    fun onSaveClicked() {
        val state = _uiState.value

        // Validation
        if (state.selectedFarm == null) {
            _uiState.value = state.copy(errorMessage = "Please select a farm")
            return
        }

        if (state.selectedProduct == null) {
            _uiState.value = state.copy(errorMessage = "Please select a product")
            return
        }

        val quantityValue = state.quantity.toDoubleOrNull()
        if (quantityValue == null || quantityValue <= 0) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid quantity")
            return
        }

        val salesmanId = authRepository.getSalesmanId()
        if (salesmanId == null) {
            _uiState.value = state.copy(errorMessage = "Not logged in")
            return
        }

        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            visitRepository.createVisit(
                salesmanId = salesmanId,
                farmId = state.selectedFarm.id,
                productId = state.selectedProduct.id,
                quantity = quantityValue,
                notes = state.notes.ifBlank { null }
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        visitSaved = true,
                        successMessage = "Visit recorded successfully"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = "Failed to save visit: ${error.message}"
                    )
                }
        }
    }

    fun onResetForm() {
        _uiState.value = VisitRecordUiState(
            farms = _uiState.value.farms,
            products = _uiState.value.products,
            isLoading = false
        )
    }
}

data class VisitRecordUiState(
    val farms: List<FarmEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val selectedFarm: FarmEntity? = null,
    val selectedProduct: ProductEntity? = null,
    val quantity: String = "",
    val notes: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val visitSaved: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
