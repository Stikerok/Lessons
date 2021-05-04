package com.example.cleanarchitechture.presentation.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.cleanarchitechture.Dependencies
import com.example.cleanarchitechture.data.cloud.NetworkResult
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.domain.usecase.person.EditPersonUseCase
import com.example.cleanarchitechture.Constants
import kotlinx.coroutines.*


class AddPersonService : Service() {
    private val personUseCase: EditPersonUseCase = Dependencies.getEditPersonUseCase()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    fun startAddPersonProcess(name: String, rating: Float) {
        ioScope.launch {
            val addPersonResult = personUseCase.addPerson(Person(name, rating))

            if (addPersonResult is NetworkResult.Success) {
                Log.d(TAG, "addPerson finished, sending broadcast")
                Intent().also {
                    it.action = Constants.PERSON_ADDED_BROADCAST
                    it.putExtra(Constants.ACTION_REQUIRED, true)
                    sendBroadcast(it)
                }
            }
        }
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }

    inner class LocalBinder : Binder() {
        fun getService(): AddPersonService = this@AddPersonService
    }

    companion object {
        const val TAG = "AddPersonService"
    }
}