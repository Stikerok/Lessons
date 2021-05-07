package com.example.cleanarchitechture.data.system.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cleanarchitechture.Constants
import com.example.cleanarchitechture.Dependencies
import com.example.cleanarchitechture.domain.entity.Person

class AddPersonWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val personsUseCase = Dependencies.getEditPersonUseCase()

    override suspend fun doWork(): Result {
        val result : Result
        val name = inputData.getString(Constants.NAME)
        val rating = inputData.getFloat(Constants.RATING, 0F)
        val person = name?.let { Person(it, rating) }
        result = if (person?.let { personsUseCase.addPerson(it) } == null) {
            Result.success()
        } else {
            Result.retry()
        }


        return result
    }
}