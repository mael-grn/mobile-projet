package com.mobileuqac.routines.data

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverter {
    private val format = SimpleDateFormat("dd/MM/yy HH:mm", Locale.FRENCH)

    @TypeConverter
    fun fromDate(date: Date): String {
        return format.format(date)
    }

    @TypeConverter
    fun toDate(dateName: String): Date {
        return try {
            format.parse(dateName) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }
}