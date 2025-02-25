package com.mobileuqac.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "routine")
class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String?,
    val description: String,
    val categorie: Categorie,
    val dateDebut: Date,
    val dateFin: Date,
    val periodicite: Periodicite,
    val priorite: Priorite,
) {
    override fun toString(): String {
        return "Nom : $nom\n" +
                "Description : $description\n" +
                "Catégorie : $categorie\n" +
                "Date de début : $dateDebut\n" +
                "Date de fin : $dateFin\n" +
                "Périodicité : $periodicite\n" +
                "Priorité : $priorite"
    }
}
