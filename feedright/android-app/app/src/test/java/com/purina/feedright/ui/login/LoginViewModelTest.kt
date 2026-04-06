package com.purina.feedright.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.purina.feedright.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mock()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Field update tests ---

    @Test
    fun `onPhoneNumberChanged updates phoneNumber in state`() = runTest {
        viewModel.onPhoneNumberChanged("+5511999990000")

        assertEquals("+5511999990000", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun `onPinChanged updates pin in state`() = runTest {
        viewModel.onPinChanged("1234")

        assertEquals("1234", viewModel.uiState.value.pin)
    }

    @Test
    fun `onPhoneNumberChanged clears existing error`() = runTest {
        // Trigger an error first
        viewModel.onLoginClicked()
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Changing the field should clear the error
        viewModel.onPhoneNumberChanged("anything")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onPinChanged clears existing error`() = runTest {
        viewModel.onLoginClicked()
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onPinChanged("1234")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    // --- Validation tests ---

    @Test
    fun `onLoginClicked shows error when phone is blank`() = runTest {
        viewModel.onPinChanged("1234")

        viewModel.onLoginClicked()

        assertEquals("Please enter phone number", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onLoginClicked shows error when PIN is blank`() = runTest {
        viewModel.onPhoneNumberChanged("+5511999990000")

        viewModel.onLoginClicked()

        assertEquals("Please enter PIN", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onLoginClicked does not call repository when phone is blank`() = runTest {
        viewModel.onPinChanged("1234")

        viewModel.onLoginClicked()

        // No interactions with repository — no exception from unexpected call
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    // --- Login success tests ---

    @Test
    fun `onLoginClicked sets isLoggedIn true on success`() = runTest {
        viewModel.onPhoneNumberChanged("+5511999990000")
        viewModel.onPinChanged("1234")
        whenever(authRepository.login("+5511999990000", "1234"))
            .thenReturn(Result.success("salesman-1"))

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.onLoginClicked()
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertTrue(success.isLoggedIn)
            assertNull(success.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Login failure tests ---

    @Test
    fun `onLoginClicked shows error message on failure`() = runTest {
        viewModel.onPhoneNumberChanged("+5511999990000")
        viewModel.onPinChanged("wrong")
        whenever(authRepository.login("+5511999990000", "wrong"))
            .thenReturn(Result.failure(Exception("Invalid credentials")))

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onLoginClicked()
            awaitItem() // loading = true

            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isLoggedIn)
            assertNotNull(errorState.errorMessage)
            assertTrue(errorState.errorMessage!!.contains("Invalid credentials"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLoginClicked sets isLoading true while waiting for result`() = runTest {
        viewModel.onPhoneNumberChanged("+5511999990000")
        viewModel.onPinChanged("1234")
        whenever(authRepository.login("+5511999990000", "1234"))
            .thenReturn(Result.success("salesman-1"))

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onLoginClicked()
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
