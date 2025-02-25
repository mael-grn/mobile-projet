package com.mobileuqac.routines.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.background

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mobileuqac.routines.HomeScreen
import com.mobileuqac.routines.Screen
import com.mobileuqac.routines.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        setContent {
            //RoutineCreationScreen(db)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineCreationScreen(db: AppDatabase, navController: NavController) {
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

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF3A3A3A)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Créer une nouvelle Routine", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom de la routine", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors( Color.White, focusedBorderColor = Color.White, cursorColor = Color.White),
            )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(Color.White, focusedBorderColor = Color.White, cursorColor = Color.White),
            minLines = 5,
            maxLines = 10
        )
        Spacer(modifier = Modifier.height(8.dp))

        DropDownSelector("Catégorie", selectedCategory.name, Categorie.entries, categoryExpanded, { categoryExpanded = it }) {
            selectedCategory = it
        }
        Spacer(modifier = Modifier.height(8.dp))

        DropDownSelector("Périodicité", selectedPeriodicity.name, Periodicite.entries, periodicityExpanded, { periodicityExpanded = it }) {
            selectedPeriodicity = it
        }
        Spacer(modifier = Modifier.height(8.dp))

        DropDownSelector("Priorité", selectedPriority.name, Priorite.entries, priorityExpanded, { priorityExpanded = it }) {
            selectedPriority = it
        }
        Spacer(modifier = Modifier.height(16.dp))
        DateTimePicker(label = "Date de début", selectedDate = dateDebut) { dateDebut = it }
        Spacer(modifier = Modifier.height(8.dp))

        DateTimePicker(label = "Date de fin", selectedDate = dateFin) { dateFin = it }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
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
                    //Toast.makeText(context, "Routine ajoutée", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Ajouter la routine", color = Color.White)
        }
    }
}



@Composable
fun <T> DropDownSelector(label: String, selectedItem: String, items: List<T>, expanded: Boolean, onExpandedChange: (Boolean) -> Unit, onItemSelected: (T) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
        .background(Color.DarkGray)
        .clickable { onExpandedChange(true) }
        .padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$label: $selectedItem", color = Color.White)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
            DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }, modifier = Modifier.background(Color.DarkGray)) {
                items.forEach { item ->
                    DropdownMenuItem(modifier = Modifier.fillMaxWidth(),text = { Text(item.toString(), color = Color.White) }, onClick = {
                        onItemSelected(item)
                        onExpandedChange(false)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(label: String, selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = selectedDate }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Button(
        onClick = { showDatePicker = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text("$label: ${selectedDate.toLocaleString()}", color = Color.White)
    }

    if (showDatePicker) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                onDateSelected(calendar.time)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}
