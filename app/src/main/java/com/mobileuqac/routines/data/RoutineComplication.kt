package com.mobileuqac.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "routine_completion")
data class RoutineCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val date: Date
)