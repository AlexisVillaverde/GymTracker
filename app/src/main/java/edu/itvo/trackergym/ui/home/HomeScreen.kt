package edu.itvo.trackergym.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.itvo.trackergym.domain.model.RoutineCatalog
import edu.itvo.trackergym.domain.model.UserProfile
import edu.itvo.trackergym.ui.components.CustomCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userProfile: UserProfile?,
    onRoutineClick: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val workoutDates by viewModel.workoutDates.collectAsState()
    val selectedSessions by viewModel.selectedDaySessions.collectAsState()

    // Obtiene las rutinas dinámicamente según la meta del usuario
    val routines = RoutineCatalog.getRoutinesForGoal(userProfile?.goal ?: "Perder peso")

    // CÁLCULO DE BMI
    val bmi = if (userProfile != null && userProfile.height > 0) {
        userProfile.weight / ((userProfile.height / 100) * (userProfile.height / 100))
    } else 0.0

    val bmiColor = if (bmi < 25) Color(0xFF4CAF50) else Color(0xFFFF9800) // Verde o Naranja

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, ${userProfile?.userName ?: "Atleta"}") },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Perfil")
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {

            // --- SECCIÓN BMI RECUPERADA ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = userProfile?.photoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("BMI: ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(
                                String.format("%.1f", bmi),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = bmiColor
                            )
                        }
                        Text("Meta: ${userProfile?.goal ?: "Sin meta"}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
            // ------------------------------

            item {
                Text("Tu Progreso", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                CustomCalendar(
                    workoutsDates = workoutDates,
                    onDateSelected = { date -> viewModel.onDateSelected(date) }
                )
            }

            if (selectedSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Entrenamientos del día:", style = MaterialTheme.typography.labelLarge)
                            selectedSessions.forEach {
                                Text("• ${it.routineName} (${it.durationSeconds / 60} mins)")
                            }
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sin actividad este día.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Rutinas Recomendadas", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(routines) { routine ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { onRoutineClick(routine.name) }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(routine.icon, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(routine.name, style = MaterialTheme.typography.titleMedium)
                            Text(routine.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            val exercisesText = "Ejercicios: " + routine.exercises.joinToString(separator = ", ") { it.name }
                            Text(
                                text = exercisesText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}