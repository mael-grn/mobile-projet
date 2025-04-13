import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileuqac.routines.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class EditRoutineUiState(
    val id: Int? = null,
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
    val isRoutineUpdated: Boolean = false,
    val errorMessage: String? = null
)

class EditRoutineViewModel(private val routineDao: RoutineDao) : ViewModel() {
    private val _uiState = MutableStateFlow(EditRoutineUiState())
    val uiState: StateFlow<EditRoutineUiState> = _uiState

    fun loadRoutine(routineId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val routine = routineDao.getById(routineId)
            routine?.let {
                _uiState.update {
                    it.copy(
                        id = routine.id,
                        name = routine.nom ?: "",
                        description = routine.description,
                        dateDebut = routine.dateDebut,
                        dateFin = routine.dateFin,
                        selectedCategory = routine.categorie,
                        selectedPeriodicity = routine.periodicite,
                        selectedPriority = routine.priorite
                    )
                }
            }
        }
    }

    fun updateName(newName: String) = _uiState.update { it.copy(name = newName) }
    fun updateDescription(newDescription: String) = _uiState.update { it.copy(description = newDescription) }
    fun updateDateDebut(newDate: Date) = _uiState.update { it.copy(dateDebut = newDate) }
    fun updateDateFin(newDate: Date) = _uiState.update { it.copy(dateFin = newDate) }
    fun updateCategory(newCategory: Categorie) = _uiState.update { it.copy(selectedCategory = newCategory) }
    fun updatePeriodicity(newPeriodicity: Periodicite) = _uiState.update { it.copy(selectedPeriodicity = newPeriodicity) }
    fun updatePriority(newPriority: Priorite) = _uiState.update { it.copy(selectedPriority = newPriority) }
    fun expandCategory(expanded: Boolean) = _uiState.update { it.copy(categoryExpanded = expanded) }
    fun expandPeriodicity(expanded: Boolean) = _uiState.update { it.copy(periodicityExpanded = expanded) }
    fun expandPriority(expanded: Boolean) = _uiState.update { it.copy(priorityExpanded = expanded) }

    fun updateRoutine() {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Le nom est obligatoire") }
            return
        }

        val updatedRoutine = Routine(
            id = _uiState.value.id ?: return,
            nom = _uiState.value.name,
            description = _uiState.value.description,
            dateDebut = _uiState.value.dateDebut,
            dateFin = _uiState.value.dateFin,
            categorie = _uiState.value.selectedCategory,
            periodicite = _uiState.value.selectedPeriodicity,
            priorite = _uiState.value.selectedPriority
        )

        viewModelScope.launch(Dispatchers.IO) {
            routineDao.update(updatedRoutine)
            _uiState.update { it.copy(isRoutineUpdated = true, errorMessage = null) }
        }
    }

    fun resetIsRoutineUpdated() = _uiState.update { it.copy(isRoutineUpdated = false) }
    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }
}
