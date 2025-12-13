package edu.itvo.trackergym.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.itvo.trackergym.ui.auth.AuthViewModel
import edu.itvo.trackergym.ui.auth.LoginScreen
import edu.itvo.trackergym.ui.auth.ProfileSetupScreen
import edu.itvo.trackergym.ui.home.EditProfileScreen
import edu.itvo.trackergym.ui.home.HomeScreen
import edu.itvo.trackergym.ui.home.HomeViewModel
import edu.itvo.trackergym.ui.workout.WorkoutTimerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    val userProfile by authViewModel.currentUser.collectAsState(initial = null)

    val startDestination = "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LaunchedEffect(userProfile) {
                if (userProfile != null) {
                    val route = if (userProfile!!.completedSetup) "home" else "setup"
                    navController.navigate(route) { popUpTo("login") { inclusive = true } }
                }
            }
            LoginScreen(viewModel = authViewModel)
        }

        composable("setup") {
            ProfileSetupScreen(
                onFinishSetup = { age, weight, height, goal ->
                    authViewModel.saveProfileData(age, weight, height, goal)
                    navController.navigate("home") { popUpTo("setup") { inclusive = true } }
                }
            )
        }

        composable("home") {
            // Si el perfil de usuario se vuelve nulo (cierre de sesión), volvemos al login
            LaunchedEffect(userProfile) {
                if (userProfile == null) {
                    navController.navigate("login") { popUpTo(navController.graph.id) { inclusive = true } }
                }
            }

            // Solo mostramos HomeScreen si el perfil no es nulo
            if (userProfile != null) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    userProfile = userProfile,
                    onRoutineClick = { routineName ->
                        navController.navigate("workout/$routineName")
                    },
                    onEditProfile = { navController.navigate("editProfile") },
                    onSignOut = { authViewModel.signOut() } // Solo notificamos el cierre de sesión
                )
            }
        }

        composable("editProfile") {
            EditProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("workout/{routineName}") { backStackEntry ->
            val routineName = backStackEntry.arguments?.getString("routineName") ?: "Entrenamiento"
            val homeViewModel: HomeViewModel = hiltViewModel()

            WorkoutTimerScreen(
                routineName = routineName,
                onFinish = { duration ->
                    homeViewModel.saveWorkout(routineName, duration)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}