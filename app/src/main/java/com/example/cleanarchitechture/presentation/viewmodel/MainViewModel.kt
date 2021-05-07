package com.example.cleanarchitechture.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cleanarchitechture.Dependencies
import com.example.cleanarchitechture.data.cloud.NetworkResult
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.domain.usecase.person.EditPersonUseCase
import com.example.cleanarchitechture.domain.usecase.person.PersonsUseCase
import com.example.cleanarchitechture.extensions.launch
import com.example.cleanarchitechture.data.system.WorkerExecutor
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.collect

class MainViewModel : ViewModel() {

    private val personUseCase: PersonsUseCase by lazy { Dependencies.getPersonsUseCase() }
    private val editPersonUseCase: EditPersonUseCase by lazy { Dependencies.getEditPersonUseCase() }


    var name: String = ""
    var rating: String = ""

    private val persons = MutableLiveData<List<Person>>(listOf())
    fun getPersons(): LiveData<List<Person>> {
        return persons
    }

    private var error = MutableLiveData<String>()
    fun getError(): LiveData<String> = error

    private val personDataReady = MutableLiveData<Pair<String, Float>>()
    fun getPersonDataReady(): LiveData<Pair<String, Float>> = personDataReady

    private val disposable = CompositeDisposable()

    init {
        updatePersons()
        launch {
            personUseCase.observePersons().collect {
                persons.value = it
            }
        }
    }

    fun addPerson() {
        launch {
            editPersonUseCase.addPerson(name, rating.toFloat())
        }
    }

    fun onPersonSelected(person: Person) {
        launch {
            editPersonUseCase.deletePerson(person)
        }
    }

    private fun <T> processNetworkResult(
        networkResult: NetworkResult<T>,
        action: (T) -> Unit
    ) {
        when (networkResult) {
            is NetworkResult.Error -> {
                error.value = networkResult.exception.message
            }
            is NetworkResult.Success -> {
                action(networkResult.data)
            }
        }
    }

    fun updatePersons() {
        launch {
            personUseCase.getPersons()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
