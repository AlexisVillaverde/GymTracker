package edu.itvo.trackergym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.itvo.trackergym.getDaysInMonth
import edu.itvo.trackergym.getFirstDayOfMonth
import edu.itvo.trackergym.isSameDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CustomCalendar(
    workoutsDates: List<Date>,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedDate by remember { mutableStateOf(Date()) }

    val daysInMonth = getDaysInMonth(currentYear, currentMonth)
    val firstDayOfWeek = getFirstDayOfMonth(currentYear, currentMonth)

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
        Calendar.getInstance().apply { set(currentYear, currentMonth, 1) }.time
    ).uppercase()

    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentMonth == 0) { currentMonth = 11; currentYear-- } else { currentMonth-- }
            }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }

            Text(monthName, fontWeight = FontWeight.Bold)

            IconButton(onClick = {
                if (currentMonth == 11) { currentMonth = 0; currentYear++ } else { currentMonth++ }
            }) { Icon(Icons.Default.ArrowForward, contentDescription = null) }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                Text(day, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalCells = daysInMonth + (firstDayOfWeek - 1)
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

        Column {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    for (col in 0 until 7) {
                        val dayNumber = (row * 7 + col) - (firstDayOfWeek - 2)

                        if (dayNumber in 1..daysInMonth) {
                            val currentDateParams = Calendar.getInstance().apply {
                                set(currentYear, currentMonth, dayNumber)
                            }
                            val hasWorkout = workoutsDates.any { isSameDay(it, currentDateParams.time) }
                            val isSelected = isSameDay(selectedDate, currentDateParams.time)

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        selectedDate = currentDateParams.time
                                        onDateSelected(currentDateParams.time)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                    if (hasWorkout && !isSelected) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Green))
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }
        }
    }
}