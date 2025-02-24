package com.mobileuqac.routines.data

import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {

    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutines()

    suspend fun insert(routine: Routine) {
        routineDao.insertRoutine(routine)
    }

    suspend fun update(routine: Routine) {
        routineDao.updateRoutine(routine)
    }

    suspend fun delete(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }
}
