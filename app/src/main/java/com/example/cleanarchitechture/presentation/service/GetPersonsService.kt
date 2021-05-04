package com.example.cleanarchitechture.presentation.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.example.cleanarchitechture.Dependencies
import com.example.cleanarchitechture.R
import com.example.cleanarchitechture.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GetPersonsService : JobIntentService() {

    private val personsUseCase = Dependencies.getPersonsUseCase()
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var notificationManager: NotificationManager

    private val getPersonsNotificationID = 102030

    override fun onHandleWork(intent: Intent) {
        notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val needToGetPersons = intent.getBooleanExtra(Constants.ACTION_REQUIRED, false)
        if (!needToGetPersons) return

        scope.launch {
            showProgressNotification()
            val workError = personsUseCase.getPersons()

            if (workError == null) {
                hideProgressNotification()
            } else {
                showErrorNotification()
            }
        }
    }

    private fun showProgressNotification() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Getting Persons")
            .setContentText("Persons update is in progress...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("We are showing progress in too long terms")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(null, getPersonsNotificationID, notification)
    }

    private fun showErrorNotification() {
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Getting Persons Error")
            .setContentText("We faced an error in getting Persons")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("We are showing progress in too long terms")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(null, getPersonsNotificationID, notification)
    }

    private fun hideProgressNotification() {
        notificationManager.cancel(getPersonsNotificationID)
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Getting Persons"
            val descriptionText = "Persons update is in progress"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}