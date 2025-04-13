package com.mobileuqac.routines.ui.views

import DropDown
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mobileuqac.routines.DateTimePicker
import com.mobileuqac.routines.data.*
import EditRoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoutineView(
    navController: NavController,
    routineId: Int,
    viewModel: EditRoutineViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadRoutine(routineId)
    }

    LaunchedEffect(uiState.isRoutineUpdated) {
        if (uiState.isRoutineUpdated) {
            Toast.makeText(context, "Routine mise à jour", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.resetIsRoutineUpdated()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Modifier la routine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
        }
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
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nom de la routine", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 10,
                shape = MaterialTheme.shapes.large,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropDown(
                label = "Catégorie",
                selectedValue = uiState.selectedCategory.name,
                items = Categorie.entries,
                expanded = uiState.categoryExpanded,
                onExpandedChange = viewModel::expandCategory,
                onItemSelected = viewModel::updateCategory
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropDown(
                label = "Périodicité",
                selectedValue = uiState.selectedPeriodicity.name,
                items = Periodicite.entries,
                expanded = uiState.periodicityExpanded,
                onExpandedChange = viewModel::expandPeriodicity,
                onItemSelected = viewModel::updatePeriodicity
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropDown(
                label = "Priorité",
                selectedValue = uiState.selectedPriority.name,
                items = Priorite.entries,
                expanded = uiState.priorityExpanded,
                onExpandedChange = viewModel::expandPriority,
                onItemSelected = viewModel::updatePriority
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateTimePicker("Date de début", uiState.dateDebut, viewModel::updateDateDebut)
            Spacer(modifier = Modifier.height(8.dp))
            DateTimePicker("Date de fin", uiState.dateFin, viewModel::updateDateFin)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = viewModel::updateRoutine,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Mettre à jour la routine",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
