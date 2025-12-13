package edu.itvo.trackergym.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

object RoutineCatalog {

    // Base de datos de ejercicios
    private val pushUps = Exercise("Flexiones", 4, 15, "Trabaja pecho, hombros y tríceps.")
    private val squats = Exercise("Sentadillas", 4, 20, "Fortalece piernas y glúteos.")
    private val burpees = Exercise("Burpees", 3, 12, "Ejercicio de cuerpo completo, muy demandante.")
    private val pullUps = Exercise("Dominadas", 3, 8, "Excelente para espalda y bíceps.")
    private val legRaises = Exercise("Elevación de piernas", 3, 20, "Fortalece el abdomen bajo.")
    private val bicepCurls = Exercise("Curl de Bíceps", 4, 12, "Aísla y fortalece los bíceps.")
    private val jumpingJacks = Exercise("Jumping Jacks", 3, 30, "Cardio ligero para calentar.")

    // Definición de las rutinas
    private val fullBodyRoutine = Routine(
        id = "1", name = "Cuerpo Completo",
        description = "Un entrenamiento balanceado para todo el cuerpo.",
        icon = Icons.Default.Accessibility,
        exercises = listOf(pushUps, squats, pullUps, legRaises)
    )

    private val cardioRoutine = Routine(
        id = "2", name = "Cardio Intenso",
        description = "Quema calorías y mejora tu resistencia.",
        icon = Icons.Default.DirectionsRun,
        exercises = listOf(jumpingJacks, burpees, squats)
    )

    private val strengthRoutine = Routine(
        id = "3", name = "Fuerza Superior",
        description = "Enfocado en el tren superior.",
        icon = Icons.Default.FitnessCenter,
        exercises = listOf(pullUps, pushUps, bicepCurls)
    )

    // Lógica para obtener rutinas según la meta
    fun getRoutinesForGoal(goal: String): List<Routine> {
        return when (goal) {
            "Perder peso" -> listOf(fullBodyRoutine, cardioRoutine)
            "Ganar músculo" -> listOf(fullBodyRoutine, strengthRoutine)
            "Mantenerse en forma" -> listOf(fullBodyRoutine, cardioRoutine, strengthRoutine)
            "Aumentar resistencia" -> listOf(cardioRoutine, fullBodyRoutine)
            else -> listOf(fullBodyRoutine) // Meta por defecto
        }
    }
}