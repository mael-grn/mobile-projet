package com.mobileuqac.routines.data

import androidx.room.TypeConverter

enum class Periodicite {
    QUOTIDIENNE, HEBDOMADAIRE, MENSUELLE, TRIMESTRIELLE, SEMESTRIELLE
}


class PeriodiciteConverter {
    @TypeConverter
    fun fromCategorie(periodicite: Periodicite): String {
        return periodicite.name
    }

    @TypeConverter
    fun toPeriodicite(periodiciteName: String): Periodicite {
        return Periodicite.valueOf(periodiciteName)
    }
}