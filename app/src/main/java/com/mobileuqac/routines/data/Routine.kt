package com.mobileuqac.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import java.util.Date

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val categorie: Categorie,
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
)
