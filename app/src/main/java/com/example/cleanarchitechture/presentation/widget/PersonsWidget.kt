package com.example.cleanarchitechture.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.cleanarchitechture.R
import com.example.cleanarchitechture.Constants
import java.util.*


class PersonsWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d(LOG_TAG, "onEnabled")
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds))

        appWidgetIds?.let {
            context?.let { nonNullContext ->
                appWidgetManager?.let { nonNullAppWidgetManager ->
                    updateWidgets(nonNullContext, nonNullAppWidgetManager, it)
                }

            }
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds))
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d(LOG_TAG, "onDisabled")
    }

    companion object {
        const val LOG_TAG = "PersonsWidget"

        fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, widgetIds: IntArray) {
            val minRating = context.getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
                .getFloat(Constants.MIN_RATING_KEY, 0F)
            for (widgetId in widgetIds) {
                val widgetView = RemoteViews(
                    context.packageName,
                    R.layout.widget
                )
                widgetView.setTextViewText(R.id.widget_title, "Min rating: $minRating")
                val adapter = Intent(context, PersonsWidgetViewService::class.java)
                widgetView.setRemoteAdapter(R.id.widget_list, adapter);

                appWidgetManager.updateAppWidget(widgetId, widgetView)
            }
        }
    }
}