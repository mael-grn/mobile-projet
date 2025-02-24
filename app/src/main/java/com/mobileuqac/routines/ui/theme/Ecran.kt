package com.mobileuqac.routines.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routineapp.viewmodel.RoutineViewModel

@Composable
fun HomeScreen(viewModel: RoutineViewModel = viewModel()) {
    val routines by viewModel.allRoutines.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mes Routines") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Naviguer vers le formulaire d’ajout */ }) {
                Text("+")
            }
        }
    ) {
        LazyColumn {
            items(routines) { routine ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = routine.name, style = MaterialTheme.typography.h6)
                        Text(text = routine.description)
                        Text(text = "Horaire: ${routine.time}")
                    }
                }
            }
        }
    }
}
