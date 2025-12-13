package edu.itvo.trackergym.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WorkoutTimerScreen(
    routineName: String,
    onFinish: (Long) -> Unit,
    onCancel: () -> Unit
) {
    var seconds by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            seconds++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Entrenando:", style = MaterialTheme.typography.titleMedium)
        Text(routineName, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(48.dp))

        val formattedTime = String.format("%02d:%02d", seconds / 60, seconds % 60)
        Text(formattedTime, fontSize = 80.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(48.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pausar" else "Reanudar")
            }
            Button(
                onClick = { onFinish(seconds) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Terminar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onCancel) { Text("Cancelar") }
    }
}