package com.example.cleanarchitechture.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.cleanarchitechture.Dependencies

class PersonsWidgetViewService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return PersonsWidgetFactory(applicationContext, intent, Dependencies.getPersonsUseCase())
    }
}