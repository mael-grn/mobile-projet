package com.mobileuqac.routines.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface NotificationDao {

    @Insert
    fun insert(notif: Notification): Long

    @Query("DELETE FROM notification WHERE idRoutine = :routineId")
    fun delAllFromRoutine(routineId: Long)

    @Query("SELECT * FROM notification WHERE idRoutine = :routineId")
    fun getAllFromRoutine(routineId: Long):List<Notification>

}