package com.purina.feedright.data.repository

import android.content.SharedPreferences
import com.purina.feedright.data.remote.AuthRequest
import com.purina.feedright.data.remote.AuthResponse
import com.purina.feedright.data.remote.FeedRightApi
import com.purina.feedright.data.remote.SalesmanDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private val api: FeedRightApi = mock()
    private val sharedPreferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()

    private lateinit var repository: AuthRepository

    private val successResponse = AuthResponse(
        token = "jwt-token-abc",
        salesman = SalesmanDto(
            id = "salesman-42",
            name = "Marco Rossi",
            phone = "+5511999990000",
            territory = "SP"
        )
    )

    @Before
    fun setUp() {
        // Wire the editor chain: edit() → putString() → apply()
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenReturn(editor)
        whenever(editor.remove(any())).thenReturn(editor)

        repository = AuthRepository(api, sharedPreferences)
    }

    // --- login ---

    @Test
    fun `login returns success with salesmanId on valid credentials`() = runTest {
        whenever(api.login(AuthRequest("+5511999990000", "1234"))).thenReturn(successResponse)

        val result = repository.login("+5511999990000", "1234")

        assertTrue(result.isSuccess)
        assertEquals("salesman-42", result.getOrThrow())
    }

    @Test
    fun `login saves auth token to shared preferences`() = runTest {
        whenever(api.login(any())).thenReturn(successResponse)

        repository.login("+5511999990000", "1234")

        verify(editor).putString("auth_token", "jwt-token-abc")
        verify(editor).apply()
    }

    @Test
    fun `login saves salesman id and name to shared preferences`() = runTest {
        whenever(api.login(any())).thenReturn(successResponse)

        repository.login("+5511999990000", "1234")

        verify(editor).putString("salesman_id", "salesman-42")
        verify(editor).putString("salesman_name", "Marco Rossi")
    }

    @Test
    fun `login returns failure when api throws`() = runTest {
        whenever(api.login(any())).thenThrow(RuntimeException("Network error"))

        val result = repository.login("+5511999990000", "1234")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()!!.message)
    }

    @Test
    fun `login does not write to shared preferences on failure`() = runTest {
        whenever(api.login(any())).thenThrow(RuntimeException("Network error"))

        repository.login("+5511999990000", "1234")

        verify(editor, never()).apply()
    }

    // --- isLoggedIn ---

    @Test
    fun `isLoggedIn returns true when token is present`() {
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn("some-token")

        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `isLoggedIn returns false when no token`() {
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn(null)

        assertFalse(repository.isLoggedIn())
    }

    // --- getSalesmanId / getSalesmanName ---

    @Test
    fun `getSalesmanId returns stored id`() {
        whenever(sharedPreferences.getString("salesman_id", null)).thenReturn("salesman-42")

        assertEquals("salesman-42", repository.getSalesmanId())
    }

    @Test
    fun `getSalesmanId returns null when not set`() {
        whenever(sharedPreferences.getString("salesman_id", null)).thenReturn(null)

        assertNull(repository.getSalesmanId())
    }

    @Test
    fun `getSalesmanName returns stored name`() {
        whenever(sharedPreferences.getString("salesman_name", null)).thenReturn("Marco Rossi")

        assertEquals("Marco Rossi", repository.getSalesmanName())
    }

    // --- logout ---

    @Test
    fun `logout removes token, salesmanId, and salesmanName from prefs`() {
        repository.logout()

        verify(editor).remove("auth_token")
        verify(editor).remove("salesman_id")
        verify(editor).remove("salesman_name")
        verify(editor).apply()
    }

    @Test
    fun `isLoggedIn returns false after logout`() {
        // Simulate logout having cleared the token
        whenever(sharedPreferences.getString("auth_token", null)).thenReturn(null)

        repository.logout()

        assertFalse(repository.isLoggedIn())
    }
}
