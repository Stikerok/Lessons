package com.example.cleanarchitechture.presentation.worker

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cleanarchitechture.Constants
import java.util.concurrent.TimeUnit


class CompletingWork {

    fun updatePersons() {
        val getPersonsWorkRequest = OneTimeWorkRequestBuilder<GetPersonsWorker>()
            .setInitialDelay(10L, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance().enqueue(getPersonsWorkRequest)
    }

    fun addPerson(name : String, rating : Float) {
        val personData = workDataOf(Constants.NAME to name, Constants.RATING to rating)

            val addPersonWorkRequest = OneTimeWorkRequestBuilder<AddPersonWorker>()
                .setInputData(personData)
                .build()
            WorkManager.getInstance().enqueue(addPersonWorkRequest)
        }

}