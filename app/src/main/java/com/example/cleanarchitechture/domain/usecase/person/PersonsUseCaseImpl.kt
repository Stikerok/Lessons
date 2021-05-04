package com.example.cleanarchitechture.domain.usecase.person

import com.example.cleanarchitechture.data.cloud.NetworkResult
import com.example.cleanarchitechture.domain.entity.Person
import io.reactivex.Flowable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class PersonsUseCaseImpl(
    private val personsRepository: PersonsRepository,
    private val personsCloudRepository: PersonsCloudRepository
) : PersonsUseCase, EditPersonUseCase {
    override fun observePersons(): Flow<List<Person>> =
        personsRepository.observePersons()

    override suspend fun getLocalPersons(): List<Person> =
        personsRepository.getPersons()

    override fun getPersonsRX(): Flowable<List<Person>> =
        personsRepository.getPersonsRX()

    override suspend fun addPerson(person: Person): NetworkResult<Person> {
        return personsCloudRepository.addPerson(person)
    }

    override suspend fun deletePerson(person: Person) {
        personsRepository.deletePerson(person)
    }

    override suspend fun getPersons(): Throwable? {
        delay(1000)
        when(val getPersonsResult = personsCloudRepository.getPersons()) {
            is NetworkResult.Error -> return getPersonsResult.exception
            is NetworkResult.Success -> {
                personsRepository.updatePersons(getPersonsResult.data)
            }
        }
        return null
    }
}
