package com.example.cleanarchitechture.presentation.viewmodel

sealed class CalculationState() {

    object Free : CalculationState()
    object Loading : CalculationState()
    object Result : CalculationState()

}