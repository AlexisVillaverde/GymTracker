package edu.itvo.trackergym.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.itvo.trackergym.domain.model.WorkoutSession
import edu.itvo.trackergym.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _workoutDates = MutableStateFlow<List<Date>>(emptyList())
    val workoutDates: StateFlow<List<Date>> = _workoutDates

    private val _selectedDaySessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val selectedDaySessions: StateFlow<List<WorkoutSession>> = _selectedDaySessions

    // Variable para saber qué día está viendo el usuario actualmente
    private var currentSelectedDate: Date = Date()

    init {
        loadCalendarData()
    }

    fun loadCalendarData() {
        viewModelScope.launch {
            _workoutDates.value = workoutRepository.getAllWorkoutDates()
            // Recargamos también las sesiones del día seleccionado actual
            onDateSelected(currentSelectedDate)
        }
    }

    fun onDateSelected(date: Date) {
        currentSelectedDate = date // Guardamos la fecha seleccionada
        viewModelScope.launch {
            _selectedDaySessions.value = workoutRepository.getWorkoutsForDate(date)
        }
    }

    fun saveWorkout(routineName: String, duration: Long) {
        viewModelScope.launch {
            val session = WorkoutSession(
                date = Date(), // Guarda con fecha y hora actual
                durationSeconds = duration,
                routineName = routineName
            )
            val result = workoutRepository.saveWorkout(session)

            if (result.isSuccess) {
                loadCalendarData()
                currentSelectedDate = Date() // Volver a hoy
                onDateSelected(currentSelectedDate)
            }
        }
    }
}