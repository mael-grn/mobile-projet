package com.mobileuqac.routines

import AddRoutineViewModel
import EditRoutineViewModel
import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.mobileuqac.routines.data.AppDatabase
import com.mobileuqac.routines.ui.theme.RoutinesTheme
import com.mobileuqac.routines.ui.viewModels.RoutineCompletionViewModel
import com.mobileuqac.routines.ui.views.AddRoutineView
import com.mobileuqac.routines.ui.views.EditRoutineView
import com.mobileuqac.routines.ui.views.HomeScreen
import com.mobileuqac.routines.ui.views.RoutineCompletionScreen
import com.mobileuqac.routines.utils.NotificationScheduler
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
// Google Calendar API
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.services.calendar.CalendarScopes
import android.app.Activity
import android.util.Log



class MainActivity() : ComponentActivity() {

    // Google Calendar API
    private lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            handleSignInResult(result.data)
        }
    }

    private lateinit var exactAlarmPermissionResult: ActivityResultLauncher<String>
    private var hasExactAlarmPermissionState = mutableStateOf(false)
    private var showExactAlarmPermissionDialog = mutableStateOf(false)

    private lateinit var notificationPermissionResult: ActivityResultLauncher<String>
    private var hasNotificationPermissionState = mutableStateOf(false)
    private var showNotificationPermissionDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gestion des permissions pour les notifications
        notificationPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasNotificationPermissionState.value = isGranted
            if (isGranted) {
                println("POST_NOTIFICATIONS permission granted")
                showNotificationPermissionDialog.value = false
            } else {
                println("POST_NOTIFICATIONS permission denied")
                showNotificationPermissionDialog.value = true
            }
        }

        // Gestion des permissions pour alarm exact
        exactAlarmPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasExactAlarmPermissionState.value = isGranted
            if (isGranted) {
                println("SCHEDULE_EXACT_ALARM permission granted")
                showExactAlarmPermissionDialog.value = false
            } else {
                println("SCHEDULE_EXACT_ALARM permission denied")
                showExactAlarmPermissionDialog.value = true
            }
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().build()

        val notificationScheduler = NotificationScheduler(this)
        val addRoutineViewModel = AddRoutineViewModel(db.routineDao(), db.notificationDao(), notificationScheduler)
        val editRoutineViewModel = EditRoutineViewModel(db.routineDao())
        val routineCompletionViewModel = RoutineCompletionViewModel(
            db.routineDao(),
            db.routineCompletionDao()
        )

        enableEdgeToEdge()


        // Google Calendar API
        // Google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(CalendarScopes.CALENDAR)
            )
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        setContent {

            RoutinesTheme {
                val navController = rememberNavController()
                val context = LocalContext.current


                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
                        notificationPermissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        hasNotificationPermissionState.value = true
                    } else {
                        hasNotificationPermissionState.value = true
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission()) {
                        exactAlarmPermissionResult.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                        hasExactAlarmPermissionState.value = alarmManager.canScheduleExactAlarms()
                    } else {
                        hasExactAlarmPermissionState.value = true
                    }
                }

                if (showNotificationPermissionDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showNotificationPermissionDialog.value = false },
                        title = { Text("Permission nécessaire") },
                        text = { Text("L'application a besoin de la permission d'envoyer des notifications pour vous rappeler vos routines. Veuillez l'autoriser dans les paramètres de l'application.") },
                        confirmButton = {
                            Button(onClick = { openAppSettings() }) {
                                Text("Ouvrir les paramètres")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showNotificationPermissionDialog.value = false }) {
                                Text("Annuler")
                            }
                        }
                    )
                }

                if (showExactAlarmPermissionDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showExactAlarmPermissionDialog.value = false },
                        title = { Text("Permission nécessaire") },
                        text = { Text("L'application a besoin de la permission de planification d'alarmes exactes pour vous envoyer des notifications ponctuelles. Veuillez l'autoriser dans les paramètres de l'application.") },
                        confirmButton = {
                            Button(
                                onClick = { openAppSettings() },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Ouvrir les paramètres")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showExactAlarmPermissionDialog.value = false },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Annuler")
                            }
                        }
                    )
                }

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
                        HomeScreen(navController, db, notificationScheduler, this@MainActivity) //Check here if theres a bug
                    }

                    composable(
                        route = Screen.EditRoutine.route,
                        arguments = listOf(navArgument("routineId") { type = NavType.IntType }),
                        enterTransition = { scaleIn(animationSpec = tween(500)) },
                        exitTransition = { scaleOut(animationSpec = tween(500)) }
                    ) { backStackEntry ->
                        val routineId = backStackEntry.arguments?.getInt("routineId") ?: return@composable
                        EditRoutineView(navController = navController, routineId = routineId, viewModel = editRoutineViewModel)
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
                        AddRoutineView(navController, addRoutineViewModel, { openAppSettings() })
                    }
                    composable(Screen.RoutineCompletions.route) { backStackEntry ->
                        val routineId = backStackEntry.arguments?.getString("routineId")?.toLongOrNull() ?: return@composable
                        RoutineCompletionScreen(navController = navController, viewModel = routineCompletionViewModel, routineId = routineId)
                    }
                }
            }
        }
    }

    // Fonction pour vérifier si la permission d'alarme exacte est accordée
    private fun hasExactAlarmPermission(): Boolean {
        val context = applicationContext
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    // Fonction pour vérifier si la permission de notification est accordée
    private fun hasNotificationPermission(): Boolean {
        val context = applicationContext
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Fonction pour ouvrir les paramètres de l'application
    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }


    // Google Calendar API
    public fun launchGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    // Google Calendar API
    private fun handleSignInResult(data: Intent?) {
        val task: Task<GoogleSignInAccount> =
            GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleSignIn", "Login successful to account ${account.email}")
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Login failed, code=${e.statusCode}", e)
        }
    }

    // Google Calendar API
    public fun ensureSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account != null && GoogleSignIn.hasPermissions(
            account,
            Scope(CalendarScopes.CALENDAR)
        )
    }

}