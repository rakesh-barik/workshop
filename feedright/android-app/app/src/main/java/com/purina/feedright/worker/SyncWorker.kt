package com.purina.feedright.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.purina.feedright.data.repository.VisitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val visitRepository: VisitRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return visitRepository.syncVisits()
            .fold(
                onSuccess = { Result.success() },
                onFailure = { Result.retry() }
            )
    }

    companion object {
        const val WORK_NAME = "feedright_sync_worker"
    }
}
