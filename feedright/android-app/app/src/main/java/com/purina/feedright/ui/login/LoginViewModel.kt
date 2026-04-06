package com.purina.feedright.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purina.feedright.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            errorMessage = null
        )
    }

    fun onPinChanged(pin: String) {
        _uiState.value = _uiState.value.copy(
            pin = pin,
            errorMessage = null
        )
    }

    fun onLoginClicked() {
        val phoneNumber = _uiState.value.phoneNumber
        val pin = _uiState.value.pin

        if (phoneNumber.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter phone number")
            return
        }

        if (pin.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter PIN")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.login(phoneNumber, pin)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Login failed: ${error.message}"
                    )
                }
        }
    }
}

data class LoginUiState(
    val phoneNumber: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)
