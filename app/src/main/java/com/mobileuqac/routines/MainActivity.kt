package com.mobileuqac.routines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.mobileuqac.routines.data.AppDatabase
import com.mobileuqac.routines.ui.RoutineCreationScreen
import com.mobileuqac.routines.ui.RoutineEditScreen
import com.mobileuqac.routines.ui.theme.RoutinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialisation de la base de données
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        enableEdgeToEdge()

        setContent {
            RoutinesTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    composable(
                        route = Screen.Home.route,
                        enterTransition = { scaleIn(animationSpec = tween(500)) },
                        exitTransition = { scaleOut(animationSpec = tween(500)) }
                    ) {
                        HomeScreen(navController, db)
                    }

                    composable(
                        route = Screen.EditRoutine.route,
                        arguments = listOf(navArgument("routineId") { type = NavType.IntType }),
                        enterTransition = { scaleIn(animationSpec = tween(500)) },
                        exitTransition = { scaleOut(animationSpec = tween(500)) }
                    ) { backStackEntry ->
                        val routineId = backStackEntry.arguments?.getInt("routineId") ?: return@composable
                        RoutineEditScreen(db = db, navController = navController, routineId = routineId)
                    }

                    composable(
                        route = Screen.AddRoutine.route,
                        enterTransition = {
                            scaleIn(
                                animationSpec = tween(500),
                                transformOrigin = TransformOrigin(1f, 1f) // BottomEnd
                            )
                        },
                        exitTransition = {
                            scaleOut(
                                animationSpec = tween(500),
                                transformOrigin = TransformOrigin(1f, 1f) // BottomEnd
                            )
                        }
                    ) {
                        RoutineCreationScreen(navController, db)
                    }
                }
            }
        }
    }
}








