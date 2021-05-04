package com.example.cleanarchitechture

import com.example.cleanarchitechture.data.cloud.CloudSource
import com.example.cleanarchitechture.data.db.LocalDatabaseSource
import com.example.cleanarchitechture.domain.usecase.person.EditPersonUseCase
import com.example.cleanarchitechture.domain.usecase.person.PersonsUseCase
import com.example.cleanarchitechture.domain.usecase.person.PersonsUseCaseImpl
import com.example.cleanarchitechture.presentation.worker.CompletingWork

object Dependencies {

    private val localDatabaseSource: LocalDatabaseSource by lazy { LocalDatabaseSource(App.instance) }
    private val cloudSource: CloudSource by lazy { CloudSource()}
    private val completingWork : CompletingWork by lazy { CompletingWork() }

    fun getPersonsUseCase(): PersonsUseCase =
        PersonsUseCaseImpl(localDatabaseSource, cloudSource)

    fun getEditPersonUseCase(): EditPersonUseCase =
        PersonsUseCaseImpl(localDatabaseSource, cloudSource)

    fun getWork(): CompletingWork {
        return completingWork
    }


}
