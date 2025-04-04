package com.mobileuqac.routines

sealed class Screen (val route : String) {
    object Home : Screen("Home")
    object AddRoutine : Screen("Add_Routine")
    object EditRoutine : Screen("Edit_Routine/{routineId}") {
        fun createRoute(routineId: Int) = "Edit_Routine/$routineId"
    }
}