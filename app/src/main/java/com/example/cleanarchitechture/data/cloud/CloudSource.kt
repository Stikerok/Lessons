package com.example.cleanarchitechture.data.cloud

import com.example.cleanarchitechture.BuildConfig
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.domain.usecase.person.PersonsCloudRepository
import com.example.cleanarchitechture.extensions.background
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class CloudSource : PersonsCloudRepository {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
    private val apiService: APIService = retrofit.create(APIService::class.java)

    override suspend fun getPersons(): NetworkResult<List<Person>> {
        var personsResponse: NetworkResult<List<Person>>
        withContext(Dispatchers.IO) {
            personsResponse = apiService.getPersons().toNetworkResult()
        }
        return  personsResponse
    }

    override suspend fun addPerson(person: Person): NetworkResult<Person>{
        var personsResponse: NetworkResult<Person>
        withContext(Dispatchers.IO) {
            personsResponse = apiService.addPerson(person).toNetworkResult()
        }
        return  personsResponse
    }

    companion object {
        private const val BASE_URL =
            "https://eu-api.backendless.com/E49B1F96-2BBB-4579-833F-7D3F5E6C84F8/E7A8D6D8-229A-4BFC-9801-F3A60E597E3B/services/Person/"
    }
}
