package com.mobileuqac.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine")
    fun getAll(): List<Routine>

    @Query("SELECT * FROM routine WHERE id = :id LIMIT 1")
    fun getById(id: Int): Routine

    @Insert
    fun insertAll(vararg routines: Routine)

    @Update
    fun update(routine: Routine)

    @Delete
    fun delete(routine: Routine)

    @Insert
    fun insert(newRoutine: Routine)
}