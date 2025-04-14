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
    fun getAllForRoutine(routineId: Long): List<RoutineCompletion>

    @Query("SELECT COUNT(*) FROM routine_completion WHERE routineId = :routineId")
    fun getTotalCompletions(routineId: Long): Int

    @Query("SELECT COUNT(*) FROM routine_completion WHERE routineId = :routineId AND date(date) = date(:targetDate)")
    fun wasCompletedOn(routineId: Long, targetDate: Date): Int
    @Delete
    fun delete(completion: RoutineCompletion)
}
