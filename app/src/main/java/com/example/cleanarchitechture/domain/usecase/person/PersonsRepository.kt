package com.example.cleanarchitechture.domain.usecase.person

import com.example.cleanarchitechture.domain.entity.Person
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

interface PersonsRepository {

    suspend fun addPerson(person: Person)
    suspend fun addPersons(persons: List<Person>)
    suspend fun deletePerson(person: Person)
    suspend fun deleteAll()
    suspend fun updatePersons(persons: List<Person>)
    fun observePersons(): Flow<List<Person>>
    suspend fun getPersons(): List<Person>
    fun getPersonsRX(): Flowable<List<Person>>

}