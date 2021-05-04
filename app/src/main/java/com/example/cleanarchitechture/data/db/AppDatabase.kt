package com.example.cleanarchitechture.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cleanarchitechture.data.db.dao.PersonDao
import com.example.cleanarchitechture.domain.entity.Person

@Database(entities = [Person::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
}