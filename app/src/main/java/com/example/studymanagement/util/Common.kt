package com.example.studymanagement.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import com.example.studymanagement.ui.theme.Green
import com.example.studymanagement.ui.theme.Red
import com.example.studymanagement.ui.theme.Yellow
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Flow


enum class Priority(val title: String, val color: Color, val value: Int) {
    Low(title = "Low", color = Green, value = 0),
    Medium(title = "Medium", color = Yellow, value = 1),
    High(title = "High", color = Red, value = 2);

    companion object{
        fun fromInt(value: Int) = values().firstOrNull{it.value == value} ?: Medium
    }
}


fun Long?.changeMillistoDateString(): String{
    val date: LocalDate = this?.let{
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

// Extension function used to convert the value of time from Long(seconds) type to Float value(Hours).
fun Long.toHours(): Float {
    val hours = this.toFloat()/3600f
    return "%.2f".format(hours).toFloat()
}

sealed class SnackbarEvent{
    data class ShowBar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarEvent()

    data object NavigateUp: SnackbarEvent()
}

// Extension function to convert Int to String value
fun Int.pad(): String{
    return this.toString().padStart(length = 2, padChar = '0')
}