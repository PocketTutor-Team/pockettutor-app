package com.github.se.project.ui.requestedLesson

import com.github.se.project.ui.lesson.parseLessonDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.junit.Assert.*
import org.junit.Test

class dateParser {
  @Test
  fun `test valid date string`() {
    // Given a valid date time string
    val timeSlot = "15/12/2024T10:30:00"

    // When the method is called
    val result = parseLessonDate(timeSlot)

    // Then it should parse the date correctly
    val expectedDate =
        LocalDateTime.parse(timeSlot, DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
    assertEquals(expectedDate, result)
  }
}
