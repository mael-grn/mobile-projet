package com.mobileuqac.routines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.routineapp.data.Routine
import com.mobileuqac.routines.data.AppDatabase
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import com.mobileuqac.routines.data.Routine
import com.mobileuqac.routines.ui.theme.RoutinesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Création d'un routine
        print("\nCréation d'une routine...")
        val routine = Routine(
            nom = "",
            description = "",
            categorie = Categorie.TRAVAIL,
            dateDebut = Date(), //date et heure actuelle
            dateFin = Date(),
            periodicite = Periodicite.QUOTIDIENNE,
            priorite = Priorite.ELEVEE
        )
        print("Routine créée : ")
        print(routine)

        //Initialisation de la base de données
        print("\nInitialisation de la base de données...")
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        print("Base de données initialisée.")

        // Sauvegarde et récupération des routines dans une coroutine
        CoroutineScope(Dispatchers.IO).launch {
            // Sauvegarde de la routine
            print("\nSavegarde de la routine dans la base de données...")
            val routineDao = db.routineDao()
            routineDao.insertAll(routine)
            print("Routine sauvegardée.")

            // Récupération de toutes les routines
            print("\nRécuperation des routines dans la base de données...")
            val routines = routineDao.getAll()
            print("Routines récupérées : ")
            for (routineDb in routines) {
                print(routineDb)
            }
        }

        enableEdgeToEdge()
        setContent {
            RoutinesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoutinesTheme {
        Greeting("Android")
    }
}