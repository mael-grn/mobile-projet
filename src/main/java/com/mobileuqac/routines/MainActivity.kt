package com.mobileuqac.routines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mobileuqac.routines.data.AppDatabase
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import com.mobileuqac.routines.data.Routine
import com.mobileuqac.routines.ui.RoutineCreationScreen
import com.mobileuqac.routines.ui.theme.RoutinesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items

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
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController, db)
                    }
                    composable(Screen.AddRoutine.route){
                        RoutineCreationScreen(db, navController)
                    }
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



@Composable
fun HomeScreen(navController: NavHostController, db: AppDatabase) {
    var routines by remember { mutableStateOf(emptyList<Routine>()) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            routines = db.routineDao().getAll()
        }
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddRoutine.route) },
                containerColor = Color.Black
            ) {
                Text("+", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF3A3A3A))
        ) {
            if (routines.isEmpty()) {
                Text(
                    "Aucune routine",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else {
                LazyColumn {
                    items(routines) { routine ->
                        RoutineItem(routine)
                    }
                }
            }
        }
    }
}



@Composable
fun RoutineItem(routine: Routine) {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            //colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Text(routine.nom ?: "", color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text("Catégorie: ${routine.categorie.name}", color = Color.LightGray)
            Text("Périodicité: ${routine.periodicite.name}", color = Color.LightGray)
        }
    }
}
