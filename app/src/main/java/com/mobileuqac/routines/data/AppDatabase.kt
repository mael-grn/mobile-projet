package com.mobileuqac.routines.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Routine::class, RoutineCompletion::class, Notification::class], version = 3)
@TypeConverters(DateConverter::class, CategorieConverter::class, PeriodiciteConverter::class, PrioriteConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun routineCompletionDao(): RoutineCompletionDao
    abstract fun notificationDao(): NotificationDao
}