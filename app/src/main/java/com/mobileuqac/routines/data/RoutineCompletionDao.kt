package com.mobileuqac.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface RoutineCompletionDao {
    @Insert
    fun insert(completion: RoutineCompletion)

    @Query("SELECT * FROM routine_completion WHERE routineId = :routineId")
    fun getAllForRoutine(routineId: Int): List<RoutineCompletion>

    @Query("SELECT COUNT(*) FROM routine_completion WHERE routineId = :routineId")
    fun getTotalCompletions(routineId: Int): Int

    @Query("SELECT COUNT(*) FROM routine_completion WHERE routineId = :routineId AND date(date) = date(:targetDate)")
    fun wasCompletedOn(routineId: Int, targetDate: Date): Int
    @Delete
    fun delete(completion: RoutineCompletion)
}
