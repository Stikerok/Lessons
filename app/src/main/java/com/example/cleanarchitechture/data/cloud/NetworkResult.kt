package com.example.cleanarchitechture.data.cloud

import retrofit2.Response
import java.lang.Exception

sealed class NetworkResult<T> {

    class Success<T>(val data: T) : NetworkResult<T>()
    class Error<T>(val exception: Throwable) : NetworkResult<T>()

}

fun <T>Response<T>.toNetworkResult() : NetworkResult<T> {
    return if (this.isSuccessful) {
        val succeedResponse = this.body()
        if (succeedResponse == null) {
            NetworkResult.Error(Exception("Response body is null"))
        } else {
            NetworkResult.Success(succeedResponse)
        }
    } else {
        NetworkResult.Error(Exception(this.raw().message))
    }
}
