package edu.itvo.trackergym.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.itvo.trackergym.domain.model.WorkoutSession
import edu.itvo.trackergym.domain.repository.WorkoutRepository
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : WorkoutRepository {

    private fun getUserId(): String = auth.currentUser?.uid ?: ""

    override suspend fun saveWorkout(session: WorkoutSession): Result<Unit> {
        return try {
            val uid = getUserId()
            if (uid.isEmpty()) throw Exception("No auth")
            db.collection("users").document(uid).collection("history")
                .add(session).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkoutsForDate(date: Date): List<WorkoutSession> {
        val uid = getUserId()
        if (uid.isEmpty()) return emptyList()

        val startCal = Calendar.getInstance().apply { time = date; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
        val endCal = Calendar.getInstance().apply { time = date; set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }

        return try {
            val snapshot = db.collection("users").document(uid).collection("history")
                .whereGreaterThanOrEqualTo("date", startCal.time)
                .whereLessThanOrEqualTo("date", endCal.time)
                .get().await()
            snapshot.toObjects(WorkoutSession::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAllWorkoutDates(): List<Date> {
        val uid = getUserId()
        if (uid.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("users").document(uid).collection("history").get().await()
            snapshot.documents.mapNotNull { it.getDate("date") }
        } catch (e: Exception) {
            emptyList()
        }
    }
}