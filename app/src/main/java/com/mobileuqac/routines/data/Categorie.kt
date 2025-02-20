package com.mobileuqac.routines.data

import androidx.room.TypeConverter

enum class Categorie {
    TRAVAIL, LOISIR, SANTE
}


class CategorieConverter {
    @TypeConverter
    fun fromCategorie(categorie: Categorie): String {
        return categorie.name
    }

    @TypeConverter
    fun toCategorie(categorieName: String): Categorie {
        return Categorie.valueOf(categorieName)
    }
}