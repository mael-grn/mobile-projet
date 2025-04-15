package com.mobileuqac.routines

import android.app.DatePickerDialog
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
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun DateTimePicker(label: String, selectedDate: Date, onDateChanged: (Date) -> Unit) {
    val context = LocalContext.current
    val tempCalendar = remember { Calendar.getInstance() }
    tempCalendar.time = selectedDate // Initialize with the provided date

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember(context) { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }

    Button(
        onClick = {
            tempCalendar.time = selectedDate
            showDatePicker = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("$label: ${dateFormatter.format(selectedDate)}", color = MaterialTheme.colorScheme.onBackground)
    }

    if (showDatePicker) {
        LaunchedEffect(showDatePicker) {
            val initialYear = tempCalendar.get(Calendar.YEAR)
            val initialMonth = tempCalendar.get(Calendar.MONTH)
            val initialDay = tempCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    tempCalendar.set(Calendar.YEAR, year)
                    tempCalendar.set(Calendar.MONTH, month)
                    tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onDateChanged(tempCalendar.time)
                    showDatePicker = false
                    showTimePicker = true
                },
                initialYear,
                initialMonth,
                initialDay
            )
            datePickerDialog.setOnDismissListener { showDatePicker = false }
            datePickerDialog.show()
        }
    }

    if (showTimePicker) {
        LaunchedEffect(showTimePicker) {
            val initialHour = tempCalendar.get(Calendar.HOUR_OF_DAY)
            val initialMinute = tempCalendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    tempCalendar.set(Calendar.MINUTE, minute)
                    onDateChanged(tempCalendar.time) // Call the provided callback
                    showTimePicker = false
                },
                initialHour,
                initialMinute,
                true
            )
            timePickerDialog.setOnDismissListener { showTimePicker = false }
            timePickerDialog.show()
        }
    }
}