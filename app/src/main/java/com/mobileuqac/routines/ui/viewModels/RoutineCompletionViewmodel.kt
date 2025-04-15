package com.mobileuqac.routines.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileuqac.routines.data.Routine
import com.mobileuqac.routines.data.RoutineCompletion
import com.mobileuqac.routines.data.RoutineCompletionDao
import com.mobileuqac.routines.data.RoutineDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutineCompletionUiState(
    val routine: Routine? = null,
    val completions: List<RoutineCompletion> = emptyList()
)

class RoutineCompletionViewModel(
    private val routineDao: RoutineDao,
    private val completionDao: RoutineCompletionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineCompletionUiState())
    val uiState: StateFlow<RoutineCompletionUiState> = _uiState

    fun loadRoutineWithCompletions(routineId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val routine = routineDao.getById(routineId.toInt())
            val completions = completionDao.getAllForRoutine(routineId)
            _uiState.update {
                it.copy(routine = routine, completions = completions)
            }
        }
    }

    fun deleteCompletion(completion: RoutineCompletion) {
        viewModelScope.launch(Dispatchers.IO) {
            completionDao.delete(completion)
            val updatedList = completionDao.getAllForRoutine(completion.routineId)
            _uiState.update { it.copy(completions = updatedList) }
        }
    }
}
