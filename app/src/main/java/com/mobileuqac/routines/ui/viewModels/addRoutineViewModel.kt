import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Notification
import com.mobileuqac.routines.data.NotificationDao
//import com.mobileuqac.routines.data.NotificationDao
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import com.mobileuqac.routines.data.Routine
import com.mobileuqac.routines.data.RoutineDao
import com.mobileuqac.routines.utils.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


import androidx.lifecycle.viewModelScope
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar as GoogleCalendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.type.DateTime
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.api.client.util.DateTime as UtilDateTime
import java.util.TimeZone



data class AddRoutineUiState(
    val name: String = "",
    val description: String = "",
    val dateDebut: Date = Date(),
    val dateFin: Date = Date(),
    val selectedCategory: Categorie = Categorie.TRAVAIL,
    val selectedPeriodicity: Periodicite = Periodicite.QUOTIDIENNE,
    val selectedPriority: Priorite = Priorite.MOYENNE,
    val categoryExpanded: Boolean = false,
    val periodicityExpanded: Boolean = false,
    val priorityExpanded: Boolean = false,
    val isRoutineAdded: Boolean = false,
    val errorMessage: String? = null,
    val showPermissionDialog: Boolean = false,
)

class AddRoutineViewModel(
    private val routineDao: RoutineDao,
    private val notificationDao: NotificationDao,
    private val notificationScheduler: NotificationScheduler,
    // Google Calendar API
    private val credential: GoogleAccountCredential
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRoutineUiState())
    val uiState: StateFlow<AddRoutineUiState> = _uiState

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun updateDateDebut(newDateDebut: Date) {
        _uiState.update { it.copy(dateDebut = newDateDebut) }
    }

    fun updateDateFin(newDateFin: Date) {
        _uiState.update { it.copy(dateFin = newDateFin) }
    }

    fun updateCategory(newCategory: Categorie) {
        _uiState.update { it.copy(selectedCategory = newCategory) }
    }

    fun updatePeriodicity(newPeriodicity: Periodicite) {
        _uiState.update { it.copy(selectedPeriodicity = newPeriodicity) }
    }

    fun updatePriority(newPriority: Priorite) {
        _uiState.update { it.copy(selectedPriority = newPriority) }
    }

    fun expandCategory(expanded: Boolean) {
        _uiState.update { it.copy(categoryExpanded = expanded) }
    }

    fun expandPeriodicity(expanded: Boolean) {
        _uiState.update { it.copy(periodicityExpanded = expanded) }
    }

    fun expandPriority(expanded: Boolean) {
        _uiState.update { it.copy(priorityExpanded = expanded) }
    }

    fun addRoutine() {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Le nom est obligatoire") }
            return
        }
        Log.d("Click", "cliqué");

        val newRoutine = Routine(
            nom = _uiState.value.name,
            description = _uiState.value.description,
            dateDebut = _uiState.value.dateDebut,
            dateFin = _uiState.value.dateFin,
            categorie = _uiState.value.selectedCategory,
            periodicite = _uiState.value.selectedPeriodicity,
            priorite = _uiState.value.selectedPriority
        )

        viewModelScope.launch(Dispatchers.IO) {
            val routineId = routineDao.insert(newRoutine)
            scheduleNotifications(routineId, newRoutine.nom.toString(), newRoutine.dateDebut, newRoutine.periodicite)
            // Google Calendar API
            try {
                insertToCalendar(newRoutine)
            } catch (e: Exception) {
                Log.e("AddRoutineViewModel", "Calendar sync failed", e)
            }
            _uiState.update { it.copy(isRoutineAdded = true, errorMessage = null) }
        }
    }

    fun resetIsRoutineAdded() {
        _uiState.update { it.copy(isRoutineAdded = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun scheduleNotifications(idRoutine: Long, nomRoutine: String, dateDebut: Date, periodicite: Periodicite) {
        viewModelScope.launch(Dispatchers.IO) {
            var deltaTemps: Long = 0

            when (periodicite) {
                Periodicite.QUOTIDIENNE -> deltaTemps = 86400000
                Periodicite.HEBDOMADAIRE -> deltaTemps = 604800000
                Periodicite.MENSUELLE -> deltaTemps = 2678400000
                Periodicite.TRIMESTRIELLE -> deltaTemps = 10713600000
                Periodicite.SEMESTRIELLE -> deltaTemps = 16070400000
            }

            try {
                for (i in 0 until 5) {
                    var dateNotifTime = dateDebut.time + (i * deltaTemps)
                    if (i == 0 && dateNotifTime <= System.currentTimeMillis()) {
                        dateNotifTime = System.currentTimeMillis() + 5000 // Planifier dans 5 secondes pour le premier
                    }
                    val notificationDate = Date(dateNotifTime)
                    val notification = Notification(
                        titre = "Rappel",
                        message = "C'est l'heure de votre routine $nomRoutine",
                        idRoutine = idRoutine,
                        date = notificationDate
                    )
                    val notificationId = notificationDao.insert(notification)
                    notificationScheduler.scheduleNotificationAt(notificationDate, notification.titre, notification.message, notificationId.toInt())
                }
            } catch (e: Exception) {
                Log.e("AddRoutineViewModel", "Erreur lors de la planification des notifications", e)
            }
        }
    }

    // Google Calendar API
    private fun insertToCalendar(routine: Routine) {
        val service = GoogleCalendar.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("Routines App")
            .build()

        if (credential.selectedAccountName != null){
            Log.d("TEST DEBUG CREDENTIAL NAME: ", credential.selectedAccountName.toString())
        }
        else {
            Log.e("TEST DEBUG CREDENTIAL NAME: ", "CREDENTIAL NAME NULL")
        }

        val event = Event()
            .setSummary(routine.nom)
            .setDescription(routine.description)

        val startDateTime = UtilDateTime(routine.dateDebut.time, TimeZone.getDefault().rawOffset / 60000)
        val endDateTime = UtilDateTime(routine.dateFin.time, TimeZone.getDefault().rawOffset / 60000)
        event.start = EventDateTime().setDateTime(startDateTime)
        event.end = EventDateTime().setDateTime(endDateTime)

        service.events().insert("primary", event).execute()

    }
}