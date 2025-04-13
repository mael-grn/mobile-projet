import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileuqac.routines.data.Categorie
import com.mobileuqac.routines.data.Periodicite
import com.mobileuqac.routines.data.Priorite
import com.mobileuqac.routines.data.Routine
import com.mobileuqac.routines.data.RoutineDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

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
    val errorMessage: String? = null
)

class AddRoutineViewModel(private val routineDao: RoutineDao) : ViewModel() {

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
            routineDao.insertAll(newRoutine)
            _uiState.update { it.copy(isRoutineAdded = true, errorMessage = null) }
        }
    }

    fun resetIsRoutineAdded() {
        _uiState.update { it.copy(isRoutineAdded = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}