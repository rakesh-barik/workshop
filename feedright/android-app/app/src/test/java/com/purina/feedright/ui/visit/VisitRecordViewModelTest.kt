package com.purina.feedright.ui.visit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.purina.feedright.data.local.entity.FarmEntity
import com.purina.feedright.data.local.entity.ProductEntity
import com.purina.feedright.data.local.entity.VisitEntity
import com.purina.feedright.data.repository.AuthRepository
import com.purina.feedright.data.repository.FarmRepository
import com.purina.feedright.data.repository.ProductRepository
import com.purina.feedright.data.repository.VisitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VisitRecordViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val visitRepository: VisitRepository = mock()
    private val farmRepository: FarmRepository = mock()
    private val productRepository: ProductRepository = mock()
    private val authRepository: AuthRepository = mock()

    private lateinit var viewModel: VisitRecordViewModel

    private val testFarm = FarmEntity(
        id = "farm-1",
        name = "Fazenda São João",
        location = "Interior SP",
        territory = "SP"
    )

    private val testProduct = ProductEntity(
        id = "prod-1",
        sku = "PUR-001",
        name = "Purina Suíno Inicial",
        category = "Suíno",
        isActive = true
    )

    private val testVisit = VisitEntity(
        id = "visit-1",
        salesmanId = "salesman-1",
        farmId = "farm-1",
        productId = "prod-1",
        quantity = 50.0,
        visitDate = "2026-04-05T10:00:00Z",
        notes = null,
        deviceId = "device-1",
        createdAt = "2026-04-05T10:00:00Z",
        syncedAt = null,
        isSynced = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(farmRepository.getAllFarms()).thenReturn(flowOf(listOf(testFarm)))
        whenever(productRepository.getActiveProducts()).thenReturn(flowOf(listOf(testProduct)))
        viewModel = VisitRecordViewModel(visitRepository, farmRepository, productRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Initial data loading ---

    @Test
    fun `farms and products are loaded on init`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(testFarm), state.farms)
        assertEquals(listOf(testProduct), state.products)
        assertFalse(state.isLoading)
    }

    // --- Field selection tests ---

    @Test
    fun `onFarmSelected updates selectedFarm`() = runTest {
        viewModel.onFarmSelected(testFarm)

        assertEquals(testFarm, viewModel.uiState.value.selectedFarm)
    }

    @Test
    fun `onProductSelected updates selectedProduct`() = runTest {
        viewModel.onProductSelected(testProduct)

        assertEquals(testProduct, viewModel.uiState.value.selectedProduct)
    }

    @Test
    fun `onQuantityChanged updates quantity`() = runTest {
        viewModel.onQuantityChanged("50.5")

        assertEquals("50.5", viewModel.uiState.value.quantity)
    }

    @Test
    fun `onNotesChanged updates notes`() = runTest {
        viewModel.onNotesChanged("Good visit")

        assertEquals("Good visit", viewModel.uiState.value.notes)
    }

    @Test
    fun `selecting a farm clears error message`() = runTest {
        viewModel.onSaveClicked() // trigger error
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onFarmSelected(testFarm)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    // --- Validation tests ---

    @Test
    fun `onSaveClicked shows error when farm not selected`() = runTest {
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please select a farm", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows error when product not selected`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onQuantityChanged("50")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please select a product", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows error when quantity is blank`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please enter a valid quantity", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows error when quantity is zero`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("0")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please enter a valid quantity", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows error when quantity is negative`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("-5")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please enter a valid quantity", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows error when quantity is non-numeric`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("abc")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")

        viewModel.onSaveClicked()

        assertEquals("Please enter a valid quantity", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onSaveClicked shows not logged in error when salesmanId is null`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        whenever(authRepository.getSalesmanId()).thenReturn(null)

        viewModel.onSaveClicked()

        assertEquals("Not logged in", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `validation errors do not call repository`() = runTest {
        // farm missing
        viewModel.onSaveClicked()

        verify(visitRepository, never()).createVisit(any(), any(), any(), any(), any())
    }

    // --- Save success tests ---

    @Test
    fun `onSaveClicked sets visitSaved true on success`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")
        whenever(visitRepository.createVisit(
            salesmanId = eq("salesman-1"),
            farmId = eq("farm-1"),
            productId = eq("prod-1"),
            quantity = eq(50.0),
            notes = eq(null)
        )).thenReturn(Result.success(testVisit))

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onSaveClicked()
            val saving = awaitItem()
            assertTrue(saving.isSaving)

            testDispatcher.scheduler.advanceUntilIdle()
            val saved = awaitItem()
            assertFalse(saved.isSaving)
            assertTrue(saved.visitSaved)
            assertNotNull(saved.successMessage)
            assertNull(saved.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSaveClicked passes notes to repository when provided`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        viewModel.onNotesChanged("Great farm visit")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")
        whenever(visitRepository.createVisit(any(), any(), any(), any(), eq("Great farm visit")))
            .thenReturn(Result.success(testVisit))

        viewModel.onSaveClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(visitRepository).createVisit(
            salesmanId = any(),
            farmId = any(),
            productId = any(),
            quantity = any(),
            notes = eq("Great farm visit")
        )
    }

    @Test
    fun `onSaveClicked passes null notes when notes field is blank`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        viewModel.onNotesChanged("   ") // whitespace only
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")
        whenever(visitRepository.createVisit(any(), any(), any(), any(), eq(null)))
            .thenReturn(Result.success(testVisit))

        viewModel.onSaveClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(visitRepository).createVisit(
            salesmanId = any(),
            farmId = any(),
            productId = any(),
            quantity = any(),
            notes = eq(null)
        )
    }

    // --- Save failure tests ---

    @Test
    fun `onSaveClicked shows error on repository failure`() = runTest {
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        whenever(authRepository.getSalesmanId()).thenReturn("salesman-1")
        whenever(visitRepository.createVisit(any(), any(), any(), any(), any()))
            .thenReturn(Result.failure(Exception("Database error")))

        viewModel.uiState.test {
            awaitItem()
            viewModel.onSaveClicked()
            awaitItem() // saving

            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertFalse(errorState.isSaving)
            assertFalse(errorState.visitSaved)
            assertNotNull(errorState.errorMessage)
            assertTrue(errorState.errorMessage!!.contains("Database error"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- Reset form test ---

    @Test
    fun `onResetForm clears selections but keeps farms and products lists`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onFarmSelected(testFarm)
        viewModel.onProductSelected(testProduct)
        viewModel.onQuantityChanged("50")
        viewModel.onNotesChanged("Some notes")

        viewModel.onResetForm()

        val state = viewModel.uiState.value
        assertNull(state.selectedFarm)
        assertNull(state.selectedProduct)
        assertEquals("", state.quantity)
        assertEquals("", state.notes)
        assertFalse(state.visitSaved)
        // Farms and products are preserved
        assertEquals(listOf(testFarm), state.farms)
        assertEquals(listOf(testProduct), state.products)
    }
}
