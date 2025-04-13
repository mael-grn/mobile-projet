package com.mobileuqac.routines.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobileuqac.routines.data.*
import com.mobileuqac.routines.data.Routine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@Composable
fun RoutineItem(routine: Routine, db: AppDatabase, onDelete: (Routine) -> Unit, onClick: (Routine) -> Unit, onViewCompletions: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cette routine ?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(routine)
                    showDialog = false
                },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                    ) {
                    Text("Oui")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                    ) {
                    Text("Non")
                }
            }
        )
    }

    Card(
        onClick = {
            onClick(routine)
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary) // Correction ici
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(routine.nom ?: "", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Catégorie: ${routine.categorie.name}", color = MaterialTheme.colorScheme.onBackground)
                    Text("Périodicité: ${routine.periodicite.name}", color = MaterialTheme.colorScheme.onBackground)
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            var completionCount by remember { mutableStateOf(0) }

            // Chargement initial du compteur
            LaunchedEffect(routine.id) {
                CoroutineScope(Dispatchers.IO).launch {
                    val count = db.routineCompletionDao().getTotalCompletions(routine.id)
                    withContext(Dispatchers.Main) {
                        completionCount = count
                    }
                }
            }

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.routineCompletionDao().insert(
                                RoutineCompletion(routineId = routine.id, date = Date())
                            )
                            // Met à jour le compteur après insertion
                            val newCount = db.routineCompletionDao().getTotalCompletions(routine.id)
                            withContext(Dispatchers.Main) {
                                completionCount = newCount
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("J’ai fait cette routine", color = Color.White)
                }
                Text("Accomplie $completionCount fois", color = Color.White)
                Button(
                    onClick = { onViewCompletions(routine.id) },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Voir les accomplissements", color = Color.White)
                }
        }
    }
}