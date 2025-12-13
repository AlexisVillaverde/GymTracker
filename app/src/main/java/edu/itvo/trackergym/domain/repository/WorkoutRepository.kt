package edu.itvo.trackergym.domain.repository

import edu.itvo.trackergym.domain.model.WorkoutSession
import java.util.Date

interface WorkoutRepository {
    suspend fun saveWorkout(session: WorkoutSession): Result<Unit>
    suspend fun getWorkoutsForDate(date: Date): List<WorkoutSession>
    suspend fun getAllWorkoutDates(): List<Date>
}