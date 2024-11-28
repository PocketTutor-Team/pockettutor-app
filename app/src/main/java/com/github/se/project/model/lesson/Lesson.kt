package com.github.se.project.model.lesson

import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Lesson(
    var id: String = "", // Unique identifier for the lesson
    val title: String = "", // Title of the lesson
    val description: String = "", // Description of the lesson
    val subject: Subject = Subject.NONE,
    val languages: List<Language> = listOf(), // Languages spoken in the lesson
    val tutorUid: List<String> = listOf(), // User ID of the tutor
    val studentUid: String = "", // User ID of the student (if booked)
    val StudentToken: String = "", // User ID for notifications
    val minPrice: Double = 0.0, // Price for the lesson
    val maxPrice: Double = 0.0, // Price for the lesson
    val price: Double = 0.0, // Price for the lesson
    val timeSlot: String = "", // Time slot for the lesson (e.g., "30/10/2024T10:00:00")
    var status: LessonStatus = LessonStatus.MATCHING, // Status of the lesson
    val latitude: Double, // Latitude for lesson location
    val longitude: Double, // Longitude for lesson location
    val rating: LessonRating? = null // Rating for the lesson
) {
  /**
   * Parses the lesson's timeSlot into a LocalDateTime object.
   *
   * @return The LocalDateTime representation of the timeSlot, or null if parsing fails.
   */
  fun parseLessonDate(): LocalDateTime? {
    return try {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
      LocalDateTime.parse(timeSlot, formatter)
    } catch (e: Exception) {
      null
    }
  }
}
