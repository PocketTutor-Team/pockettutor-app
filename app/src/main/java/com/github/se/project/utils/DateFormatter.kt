package com.github.se.project.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public fun formatDate(timeSlot: String): String {
    return try {
        val dateTime =
            LocalDateTime.parse(timeSlot, DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
        dateTime.format(DateTimeFormatter.ofPattern("EEEE, d MMMM • HH:mm"))
    } catch (e: Exception) {
        timeSlot
    }
}