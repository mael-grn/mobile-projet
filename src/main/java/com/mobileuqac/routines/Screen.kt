package com.mobileuqac.routines

sealed class Screen (val route : String) {
    object Home : Screen("Home")
    object AddRoutine : Screen("Add_Routine")
}