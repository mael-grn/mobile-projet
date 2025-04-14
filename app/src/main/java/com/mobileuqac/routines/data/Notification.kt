package com.mobileuqac.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notification")
class Notification(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val titre: String,
    val message: String,
    val idRoutine: Long,
    val date: Date,
) {
    override fun toString(): String {
        return "Titre : $titre\n" +
                "Message : $message\n" +
                "Id Routine : $idRoutine\n" +
                "Date : $date"
    }
}