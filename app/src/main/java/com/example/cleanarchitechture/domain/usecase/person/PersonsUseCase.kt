package com.example.cleanarchitechture.domain.usecase.person

import com.example.cleanarchitechture.data.cloud.NetworkResult
import com.example.cleanarchitechture.domain.entity.Person
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

interface PersonsUseCase {
    fun observePersons(): Flow<List<Person>>
    suspend fun getLocalPersons(): List<Person>
    fun getPersonsRX(): Flowable<List<Person>>
    suspend fun getPersons(): Throwable?
}