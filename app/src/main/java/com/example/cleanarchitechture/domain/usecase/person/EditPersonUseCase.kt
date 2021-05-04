package com.example.cleanarchitechture.domain.usecase.person

import com.example.cleanarchitechture.data.cloud.NetworkResult
import com.example.cleanarchitechture.domain.entity.Person

interface EditPersonUseCase {
    suspend fun addPerson(person: Person): NetworkResult<Person>
    suspend fun deletePerson(person: Person)
}