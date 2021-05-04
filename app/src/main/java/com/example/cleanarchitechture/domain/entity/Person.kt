package com.example.cleanarchitechture.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person(
    @PrimaryKey val name: String,
    var rating: Float
)
