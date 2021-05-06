package com.example.cleanarchitechture.data.system

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cleanarchitechture.Constants
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.domain.usecase.person.PersonWorkExecutor
import com.example.cleanarchitechture.data.system.worker.AddPersonWorker
import com.example.cleanarchitechture.data.system.worker.GetPersonsWorker
import java.util.concurrent.TimeUnit


class WorkerExecutor(private val context: Context) : PersonWorkExecutor {

    fun updatePersons() {
        val getPersonsWorkRequest = OneTimeWorkRequestBuilder<GetPersonsWorker>()
            .setInitialDelay(10L, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(getPersonsWorkRequest)
    }

    override fun addPerson(person: Person) {
        val personData = workDataOf(
            Constants.NAME to person.name,
            Constants.RATING to person.rating
        )

        val addPersonWorkRequest = OneTimeWorkRequestBuilder<AddPersonWorker>()
            .setInputData(personData)
            .build()
        WorkManager.getInstance(context).enqueue(addPersonWorkRequest)
    }

}