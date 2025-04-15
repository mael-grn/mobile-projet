package com.mobileuqac.routines

sealed class Screen (val route : String) {
    object Home : Screen("Home")
    object AddRoutine : Screen("Add_Routine")
    object EditRoutine : Screen("Edit_Routine/{routineId}") {
        fun createRoute(routineId: Long) = "Edit_Routine/$routineId"
    }
    object RoutineCompletions : Screen("Routine_Completions/{routineId}") {
        fun createRoute(routineId: Int) = "Routine_Completions/$routineId"
    }
}