package edu.itvo.trackergym.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Date

data class UserProfile(
    val uid: String = "",
    val userName: String = "Atleta",
    val photoUrl: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val goal: String = "Perder peso", // Meta predeterminada
    val completedSetup: Boolean = false
)

data class WorkoutSession(
    val id: String = "",
    val date: Date = Date(),
    val durationSeconds: Long = 0,
    val routineName: String = ""
)

// Nuevo modelo para los ejercicios
data class Exercise(
    val name: String,
    val sets: Int, // Series
    val reps: Int, // Repeticiones
    val description: String
)

data class Routine(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val exercises: List<Exercise> // Las rutinas ahora tienen una lista de ejercicios
)
