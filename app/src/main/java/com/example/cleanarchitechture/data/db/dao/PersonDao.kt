package com.example.cleanarchitechture.data.db.dao

import androidx.room.*
import androidx.room.FtsOptions.Order
import com.example.cleanarchitechture.domain.entity.Person
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow


@Dao
interface PersonDao {
    @Query("SELECT * FROM Person")
    fun observeAll(): Flow<List<Person>>

    @Query("SELECT * FROM Person")
    fun getAllRX(): Flowable<List<Person>>

    @Query("SELECT * FROM Person")
    fun getAll(): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(persons: List<Person>)

    @Delete
    fun delete(person: Person)

    @Query("DELETE FROM Person")
    fun deleteAll()

    @Transaction
    fun updatePersons(persons: List<Person>) {
        deleteAll()
        insertAll(persons)
    }
}