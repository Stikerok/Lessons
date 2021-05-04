package com.example.cleanarchitechture.presentation.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cleanarchitechture.Constants
import com.example.cleanarchitechture.Dependencies
import com.example.cleanarchitechture.domain.entity.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddPersonWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private val personsUseCase = Dependencies.getEditPersonUseCase()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun doWork(): Result {
        var result = Result.success()
        val name = inputData.getString(Constants.NAME)
        val rating = inputData.getFloat(Constants.RATING, 0F)
        val person = name?.let { Person(it,rating) }
        scope.launch {
            result = if (person?.let { personsUseCase.addPerson(it) } == null) {
                Result.success()
            } else {
                Result.retry()
            }
        }

        return result
    }
}