package com.example.cleanarchitechture.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanarchitechture.R
import com.example.cleanarchitechture.Constants


class WidgetSetupActivity : AppCompatActivity() {
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate config")


        widgetID = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            val cancelIntent = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            }

            setResult(RESULT_CANCELED, cancelIntent)
            finish()
        }

        setContentView(R.layout.widget_setup_activity)
        findViewById<View>(R.id.ok_btn).setOnClickListener {
            getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE).edit()
                .apply {
                    putFloat(
                        Constants.MIN_RATING_KEY,
                        findViewById<EditText>(R.id.min_rating_input).text.toString().toFloat()
                    )
                }
                .apply()
            val appWidgetManager = AppWidgetManager.getInstance(this)
            PersonsWidget.updateWidgets(this, appWidgetManager, intArrayOf(widgetID))
            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                }
            )
            finish()
        }
    }

    companion object {
        const val LOG_TAG = "WidgetSetupActivity";
    }
}