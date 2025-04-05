package com.mobileuqac.routines.ui

import DropDown
import androidx.compose.foundation.background
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mobileuqac.routines.DateTimePicker
import com.mobileuqac.routines.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import androidx.compose.material.icons.filled.Add


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineCreationScreen(navController: NavController, db: AppDatabase) {

    val routineDao = db.routineDao()
    val context = LocalContext.current

    var name by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var dateDebut by remember { mutableStateOf(Date()) }
    var dateFin by remember { mutableStateOf(Date()) }
    var selectedCategory by remember { mutableStateOf(Categorie.TRAVAIL) }
    var selectedPeriodicity by remember { mutableStateOf(Periodicite.QUOTIDIENNE) }
    var selectedPriority by remember { mutableStateOf(Priorite.MOYENNE) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var periodicityExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ajouter une routine",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom de la routine", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth(),
                minLines = 5,
                maxLines = 10,
                shape = MaterialTheme.shapes.large,
            )
            Spacer(modifier = Modifier.height(16.dp))

            DropDown("Catégorie", selectedCategory.name, Categorie.entries, categoryExpanded, { categoryExpanded = it }) {
                selectedCategory = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            DropDown("Périodicité", selectedPeriodicity.name, Periodicite.entries, periodicityExpanded, { periodicityExpanded = it }) {
                selectedPeriodicity = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            DropDown("Priorité", selectedPriority.name, Priorite.entries, priorityExpanded, { priorityExpanded = it }) {
                selectedPriority = it
            }

            Spacer(modifier = Modifier.height(16.dp))

            DateTimePicker(
                label = "Date de début",
                selectedDate = dateDebut,
            ) { dateDebut = it }

            Spacer(modifier = Modifier.height(8.dp))

            DateTimePicker(
                label = "Date de fin",
                selectedDate = dateFin
            ) { dateFin = it }

            Spacer(modifier = Modifier.weight(1f)) // Occupy remaining space

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    if (name.text.isBlank()) {
                        Toast.makeText(context, "Le nom est obligatoire", Toast.LENGTH_SHORT).show()
                    } else {
                        val newRoutine = Routine(
                            nom = name.text,
                            description = description.text,
                            dateDebut = dateDebut,
                            dateFin = dateFin,
                            categorie = selectedCategory,
                            periodicite = selectedPeriodicity,
                            priorite = selectedPriority
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            routineDao.insertAll(newRoutine)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Routine ajoutée", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Ajouter la routine",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between text and icon
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
