package com.example.cleanarchitechture.presentation.widget


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanarchitechture.Constants
import com.example.cleanarchitechture.R
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.domain.usecase.person.PersonsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.min


class PersonsWidgetFactory(
    private val context: Context,
    intent: Intent,
    private val personsUseCase: PersonsUseCase
) :
    RemoteViewsService.RemoteViewsFactory {
    private var persons: List<Person> = listOf()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var minRating = 0f

    override fun onCreate() {
        persons = listOf()
        val prefs = context.getSharedPreferences(Constants.SP_NAME, AppCompatActivity.MODE_PRIVATE)
        minRating = prefs.getFloat(Constants.MIN_RATING_KEY, 0f)
    }

    override fun onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged")
        var filteredPersons: List<Person> = emptyList()
        scope.launch {
            filteredPersons = personsUseCase.getLocalPersons()
                .filter { it.rating >= minRating }
                .sortedByDescending { it.rating }
            Log.d(TAG, "onDataSetChanged ${filteredPersons.size}")
            persons = filteredPersons
        }
    }

    override fun onDestroy() {
        scope.cancel()
    }

    override fun getCount(): Int {
        Log.d(TAG, "getCount ${persons.size}")
        return persons.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        Log.d(TAG, "getViewAt $position")
        val person = persons[position]
        val itemView = RemoteViews(
            context.packageName,
            R.layout.widget_item
        )
        itemView.setTextViewText(R.id.widget_item_name, person.name)
        itemView.setTextViewText(R.id.widget_item_rating, "${person.rating}")
        return itemView
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    companion object {
        const val TAG = "PersonsWidget"
    }
}