package com.example.cleanarchitechture.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

suspend fun <T> background(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO) {
        block()
    }
}

fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch {
        block()
    }
}
