package com.mobileuqac.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import java.util.Date

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val description: String,
    val categorie: Categorie,
    val DateDebut: Date,
    val DateFin: Date,
    val periodicite: Periodicite,
    val priorite: Priorite,
)
