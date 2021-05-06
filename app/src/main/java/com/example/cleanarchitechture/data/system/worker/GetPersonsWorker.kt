package com.example.cleanarchitechture.data.system.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cleanarchitechture.Dependencies

class GetPersonsWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val personsUseCase = Dependencies.getPersonsUseCase()

    override suspend fun doWork(): Result {
        return if (personsUseCase.getPersons() == null) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}