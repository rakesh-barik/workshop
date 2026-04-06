package com.purina.feedright.data.repository

import com.purina.feedright.data.local.dao.VisitDao
import com.purina.feedright.data.local.entity.VisitEntity
import com.purina.feedright.data.remote.FeedRightApi
import com.purina.feedright.data.remote.SyncResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VisitRepositoryTest {

    private val visitDao: VisitDao = mock()
    private val api: FeedRightApi = mock()
    private val deviceId = "test-device-id"

    private val repository = VisitRepository(visitDao, api, deviceId)

    private fun makeVisit(
        id: String = "v-1",
        isSynced: Boolean = false,
        salesmanId: String = "s-1",
        farmId: String = "f-1",
        productId: String = "p-1",
        quantity: Double = 10.0
    ) = VisitEntity(
        id = id,
        salesmanId = salesmanId,
        farmId = farmId,
        productId = productId,
        quantity = quantity,
        visitDate = "2026-04-05T10:00:00Z",
        notes = null,
        deviceId = deviceId,
        createdAt = "2026-04-05T10:00:00Z",
        syncedAt = null,
        isSynced = isSynced
    )

    // --- getRecentVisits ---

    @Test
    fun `getRecentVisits delegates to dao`() = runTest {
        val visits = listOf(makeVisit())
        whenever(visitDao.getRecentVisits()).thenReturn(flowOf(visits))

        // Doesn't throw and returns the dao's flow
        val flow = repository.getRecentVisits()
        verify(visitDao).getRecentVisits()
    }

    // --- createVisit ---

    @Test
    fun `createVisit inserts visit into dao with correct fields`() = runTest {
        val captor = argumentCaptor<VisitEntity>()

        val result = repository.createVisit(
            salesmanId = "s-1",
            farmId = "f-1",
            productId = "p-1",
            quantity = 42.5,
            notes = "Test note"
        )

        assertTrue(result.isSuccess)
        verify(visitDao).insert(captor.capture())
        val saved = captor.firstValue
        assertEquals("s-1", saved.salesmanId)
        assertEquals("f-1", saved.farmId)
        assertEquals("p-1", saved.productId)
        assertEquals(42.5, saved.quantity)
        assertEquals("Test note", saved.notes)
        assertEquals(deviceId, saved.deviceId)
        assertFalse(saved.isSynced)
    }

    @Test
    fun `createVisit returns the saved entity on success`() = runTest {
        val result = repository.createVisit(
            salesmanId = "s-1",
            farmId = "f-1",
            productId = "p-1",
            quantity = 10.0,
            notes = null
        )

        assertTrue(result.isSuccess)
        val visit = result.getOrThrow()
        assertEquals("s-1", visit.salesmanId)
        assertEquals(10.0, visit.quantity)
    }

    @Test
    fun `createVisit stores null notes when omitted`() = runTest {
        val captor = argumentCaptor<VisitEntity>()

        repository.createVisit("s-1", "f-1", "p-1", 10.0, notes = null)

        verify(visitDao).insert(captor.capture())
        assertEquals(null, captor.firstValue.notes)
    }

    @Test
    fun `createVisit returns failure when dao throws`() = runTest {
        whenever(visitDao.insert(any())).thenThrow(RuntimeException("DB full"))

        val result = repository.createVisit("s-1", "f-1", "p-1", 10.0)

        assertTrue(result.isFailure)
        assertEquals("DB full", result.exceptionOrNull()!!.message)
    }

    // --- syncVisits ---

    @Test
    fun `syncVisits returns success(0) when no unsynced visits`() = runTest {
        whenever(visitDao.getUnsyncedVisits()).thenReturn(emptyList())

        val result = repository.syncVisits()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
        verify(api, never()).syncVisits(any())
    }

    @Test
    fun `syncVisits sends only unsynced visits to api`() = runTest {
        val unsynced = listOf(makeVisit(id = "v-1"), makeVisit(id = "v-2"))
        whenever(visitDao.getUnsyncedVisits()).thenReturn(unsynced)
        whenever(api.syncVisits(any())).thenReturn(
            SyncResponse(synced = 2, failed = 0, errors = emptyList())
        )

        repository.syncVisits()

        val captor = argumentCaptor<com.purina.feedright.data.remote.SyncRequest>()
        verify(api).syncVisits(captor.capture())
        assertEquals(2, captor.firstValue.visits.size)
    }

    @Test
    fun `syncVisits marks each attempted visit as synced`() = runTest {
        val unsynced = listOf(makeVisit(id = "v-1"), makeVisit(id = "v-2"))
        whenever(visitDao.getUnsyncedVisits()).thenReturn(unsynced)
        whenever(api.syncVisits(any())).thenReturn(
            SyncResponse(synced = 2, failed = 0, errors = emptyList())
        )

        repository.syncVisits()

        verify(visitDao).markAsSynced(eq("v-1"), any())
        verify(visitDao).markAsSynced(eq("v-2"), any())
    }

    @Test
    fun `syncVisits returns count of synced visits`() = runTest {
        whenever(visitDao.getUnsyncedVisits()).thenReturn(
            listOf(makeVisit("v-1"), makeVisit("v-2"), makeVisit("v-3"))
        )
        whenever(api.syncVisits(any())).thenReturn(
            SyncResponse(synced = 3, failed = 0, errors = emptyList())
        )

        val result = repository.syncVisits()

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow())
    }

    @Test
    fun `syncVisits returns failure when api throws`() = runTest {
        whenever(visitDao.getUnsyncedVisits()).thenReturn(listOf(makeVisit()))
        whenever(api.syncVisits(any())).thenThrow(RuntimeException("No network"))

        val result = repository.syncVisits()

        assertTrue(result.isFailure)
        assertEquals("No network", result.exceptionOrNull()!!.message)
    }

    @Test
    fun `syncVisits does not mark visits as synced when api fails`() = runTest {
        whenever(visitDao.getUnsyncedVisits()).thenReturn(listOf(makeVisit()))
        whenever(api.syncVisits(any())).thenThrow(RuntimeException("No network"))

        repository.syncVisits()

        verify(visitDao, never()).markAsSynced(any(), any())
    }

    // --- getUnsyncedCount ---

    @Test
    fun `getUnsyncedCount delegates to dao`() = runTest {
        whenever(visitDao.getUnsyncedCount()).thenReturn(3)

        val count = repository.getUnsyncedCount()

        assertEquals(3, count)
    }
}
