package com.mobileuqac.routines.data

import androidx.room.TypeConverter

enum class Priorite {
    FAIBLE, MOYENNE, ELEVEE
}

class PrioriteConverter {
    @TypeConverter
    fun fromPriorite(priorite: Priorite): String {
        return priorite.name
    }

    @TypeConverter
    fun toPriorite(prioriteName: String): Priorite {
        return Priorite.valueOf(prioriteName)
    }
}