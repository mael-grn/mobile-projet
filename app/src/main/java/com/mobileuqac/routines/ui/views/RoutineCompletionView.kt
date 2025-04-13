package com.mobileuqac.routines.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mobileuqac.routines.data.*
import com.mobileuqac.routines.data.Routine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineCompletionScreen(
    db: AppDatabase,
    routineId: Int,
    navController: NavHostController
) {
    val context = LocalContext.current
    var completions by remember { mutableStateOf<List<RoutineCompletion>>(emptyList()) }
    var routine by remember { mutableStateOf<Routine?>(null) }

    LaunchedEffect(routineId) {
        CoroutineScope(Dispatchers.IO).launch {
            val r = db.routineDao().getById(routineId)
            val list = db.routineCompletionDao().getAllForRoutine(routineId)
            withContext(Dispatchers.Main) {
                routine = r
                completions = list
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = routine?.nom ?: "Routine", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (completions.isEmpty()) {
                Text("Aucun accomplissement enregistré.")
            } else {
                LazyColumn {
                    items(completions) { completion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(completion.date.toLocaleString())

                            Button(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        db.routineCompletionDao().delete(completion)
                                        val updatedList = db.routineCompletionDao().getAllForRoutine(routineId)
                                        withContext(Dispatchers.Main) {
                                            completions = updatedList
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Supprimer", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
