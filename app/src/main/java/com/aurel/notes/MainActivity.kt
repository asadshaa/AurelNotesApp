package com.aurel.notes

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aurel.notes.core.datastore.AurelUserPreferences
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.ui.navigation.AurelDestination
import com.aurel.notes.ui.navigation.AurelNavigationShell
import com.aurel.notes.ui.editor.EditorScreen
import com.aurel.notes.ui.home.HomeScreen
import com.aurel.notes.ui.notes.NotesScreen
import com.aurel.notes.ui.notebooks.NotebooksScreen
import com.aurel.notes.ui.tasks.TasksScreen
import com.aurel.notes.ui.calendar.CalendarScreen
import com.aurel.notes.ui.journal.JournalScreen
import com.aurel.notes.ui.canvas.CanvasScreen
import com.aurel.notes.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AurelApplication
        val prefsRepo = app.preferencesRepository

        setContent {
            val userPrefs by prefsRepo.userPreferencesFlow.collectAsState(initial = AurelUserPreferences())

            // Dynamic FLAG_SECURE for privacy mode
            if (userPrefs.privacyModeFlagSecure) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }

            val windowSizeClass = calculateWindowSizeClass(this)
            val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            val startDestination = if (userPrefs.onboardingCompleted) {
                AurelDestination.Home.route
            } else {
                AurelDestination.Onboarding.route
            }

            AurelTheme(
                themeMode = userPrefs.themeMode,
                fontScale = userPrefs.fontScale,
                lineHeightMultiplier = userPrefs.lineHeightMultiplier,
                editorFont = userPrefs.editorFont,
                reducedMotion = userPrefs.reducedMotion
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                AurelNavigationShell(
                    currentRoute = currentRoute,
                    isExpandedScreen = isExpanded,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(AurelDestination.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onQuickCaptureClick = {
                        navController.navigate(AurelDestination.createEditorRoute(0L))
                    }
                ) {
                    AurelNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        onCompleteOnboarding = {
                            coroutineScope.launch {
                                prefsRepo.setOnboardingCompleted(true)
                            }
                            navController.navigate(AurelDestination.Home.route) {
                                popUpTo(AurelDestination.Onboarding.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AurelNavHost(
    navController: NavHostController,
    startDestination: String = AurelDestination.Home.route,
    onCompleteOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AurelDestination.Onboarding.route) {
            com.aurel.notes.ui.onboarding.OnboardingScreen(
                onCompleteOnboarding = onCompleteOnboarding
            )
        }
        composable(AurelDestination.Home.route) {
            HomeScreen(
                onNavigateToEditor = { noteId ->
                    navController.navigate(AurelDestination.createEditorRoute(noteId))
                },
                onNavigateToNotes = {
                    navController.navigate(AurelDestination.Notes.route)
                },
                onNavigateToTasks = {
                    navController.navigate(AurelDestination.Tasks.route)
                },
                onNavigateToNotebooks = {
                    navController.navigate(AurelDestination.Notebooks.route)
                },
                onNavigateToCanvas = { noteId ->
                    navController.navigate(AurelDestination.createCanvasRoute(noteId))
                }
            )
        }
        composable(AurelDestination.Notes.route) {
            NotesScreen(
                onNavigateToEditor = { noteId ->
                    navController.navigate(AurelDestination.createEditorRoute(noteId))
                }
            )
        }
        composable(AurelDestination.Notebooks.route) {
            NotebooksScreen(
                onNotebookSelected = { notebookId ->
                    navController.navigate(AurelDestination.Notes.route)
                }
            )
        }
        composable(AurelDestination.Tasks.route) {
            TasksScreen()
        }
        composable(AurelDestination.Calendar.route) {
            CalendarScreen(
                onNavigateToEditor = { noteId ->
                    navController.navigate(AurelDestination.createEditorRoute(noteId))
                }
            )
        }
        composable(AurelDestination.createJournalRoute()) {
            JournalScreen(
                onNavigateBack = { navController.popBackStack() },
                onSavedOpenEditor = { noteId ->
                    navController.navigate(AurelDestination.createEditorRoute(noteId))
                }
            )
        }
        composable(AurelDestination.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOnboarding = {
                    navController.navigate(AurelDestination.Onboarding.route)
                }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) {
            EditorScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLinkedNote = { linkedNoteId ->
                    navController.navigate(AurelDestination.createEditorRoute(linkedNoteId))
                }
            )
        }
        composable(
            route = "canvas/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) {
            CanvasScreen(
                onNavigateBack = { navController.popBackStack() },
                onSavedDrawing = { path ->
                    navController.popBackStack()
                }
            )
        }
    }
}
