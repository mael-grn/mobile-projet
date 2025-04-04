package com.mobileuqac.routines

import android.app.TimePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Date

@Composable
fun DateTimePicker(label: String, selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val context = LocalContext.current
    val tempCalendar = remember { Calendar.getInstance() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Button(
        onClick = {
            tempCalendar.time = selectedDate
            showDatePicker = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("$label: ${selectedDate.toLocaleString()}", color = MaterialTheme.colorScheme.onBackground)
    }

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    tempCalendar.set(Calendar.YEAR, year)
                    tempCalendar.set(Calendar.MONTH, month)
                    tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    showDatePicker = false
                    showTimePicker = true
                },
                tempCalendar.get(Calendar.YEAR),
                tempCalendar.get(Calendar.MONTH),
                tempCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    if (showTimePicker) {
        LaunchedEffect(Unit) {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    tempCalendar.set(Calendar.MINUTE, minute)
                    onDateSelected(tempCalendar.time)
                    showTimePicker = false
                },
                tempCalendar.get(Calendar.HOUR_OF_DAY),
                tempCalendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}