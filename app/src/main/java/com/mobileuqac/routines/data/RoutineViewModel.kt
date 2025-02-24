package com.mobileuqac.routines.data

import android.app.Application
import androidx.constraintlayout.helper.widget.Flow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routineapp.data.*
import kotlinx.coroutines.launch

class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoutineRepository
    val allRoutines: Flow<List<Routine>>

    init {
        val dao = RoutineDatabase.getDatabase(application).routineDao()
        repository = RoutineRepository(dao)
        allRoutines = repository.allRoutines
    }

    fun insert(routine: Routine) = viewModelScope.launch {
        repository.insert(routine)
    }

    fun update(routine: Routine) = viewModelScope.launch {
        repository.update(routine)
    }

    fun delete(routine: Routine) = viewModelScope.launch {
        repository.delete(routine)
    }
}
