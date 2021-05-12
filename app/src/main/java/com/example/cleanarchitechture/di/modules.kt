package com.example.cleanarchitechture.di

import com.example.cleanarchitechture.data.cloud.CloudSource
import com.example.cleanarchitechture.data.db.LocalDatabaseSource
import com.example.cleanarchitechture.data.system.WorkerExecutor
import com.example.cleanarchitechture.domain.usecase.person.*
import com.example.cleanarchitechture.presentation.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.experimental.builder.factoryBy

val appModule = module {
    single {
        CloudSource()
    }
    single {
        LocalDatabaseSource(androidContext())
    }
    single {
        WorkerExecutor(androidContext())
    }
    factoryBy<PersonsCloudRepository,CloudSource>()
    factoryBy<PersonsRepository,LocalDatabaseSource>()
    factoryBy<PersonsUseCase,PersonsUseCaseImpl>()
    factoryBy<EditPersonUseCase,PersonsUseCaseImpl>()
    factoryBy<PersonWorkExecutor,WorkerExecutor>()
    factory {
        PersonsUseCaseImpl(get(),get(),get())
    }

}

val viewModelModel = module {
    viewModel {
        MainViewModel(get(), get())
    }
}