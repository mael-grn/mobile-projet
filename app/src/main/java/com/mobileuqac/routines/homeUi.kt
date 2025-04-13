package com.mobileuqac.routines
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mobileuqac.routines.data.AppDatabase
import com.mobileuqac.routines.data.Routine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, db: AppDatabase) {

    // Liste des routines
    var routines by remember { mutableStateOf(emptyList<Routine>()) }

    //récupération des routines dans la base de données au lancement
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            routines = db.routineDao().getAll()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddRoutine.route) },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Text("+", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

        ) {
            if (routines.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, // Pour centrer horizontalement
                    verticalArrangement = Arrangement.Center // Pour centrer verticalement
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info, // Icône à utiliser
                        contentDescription = "Icône d'information",
                        modifier = Modifier.align(Alignment.CenterHorizontally), // Centrer l'icône
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aucune routine",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Appuyez sur le bouton + pour ajouter une nouvelle routine",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                LazyColumn {
                    items(routines) { routine ->
                        RoutineItem(
                            routine = routine,
                            db = db,
                            onDelete = { toDelete ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.routineDao().delete(toDelete)
                                    routines = db.routineDao().getAll()
                                }
                            },
                            onClick = { toEdit ->
                                navController.navigate(Screen.EditRoutine.createRoute(toEdit.id))
                            },
                                    onViewCompletions = { routineId ->
                                navController.navigate(Screen.RoutineCompletions.createRoute(routineId))
                            }
                        )
                    }
                }
            }
        }
    }
}