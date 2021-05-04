package com.example.cleanarchitechture.presentation.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cleanarchitechture.Dependencies
import kotlinx.coroutines.*

class GetPersonsWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private val personsUseCase = Dependencies.getPersonsUseCase()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun doWork(): Result {
        var result = Result.success()

        scope.launch {
            result = if (personsUseCase.getPersons() == null) {
                Result.success()
            } else {
                Result.retry()
            }
        }

        return result
    }
}