package com.github.se.project.model.lesson

import com.github.se.project.model.profile.TutoringSubject

data class Lesson(
    val id: String = "", // Unique identifier for the lesson
    val title: String = "", // Title of the lesson
    val description: String = "", // Description of the lesson
    val subject: TutoringSubject = TutoringSubject.PHYSICS,
    val tutorUid: String = "", // User ID of the tutor
    val studentUid: String = "", // User ID of the student (if booked)
    val minPrice: Double = 0.0, // Price for the lesson
    val maxPrice: Double = 0.0, // Price for the lesson
    val timeSlot: String = "", // Time slot for the lesson (e.g., "2024-10-10T10:00:00")
    val status: LessonStatus = LessonStatus.PENDING, // Status of the lesson
    val language: String = "" // todo: create a language enum ?
)

// Enum class to define lesson status
enum class LessonStatus {
  REQUESTED,
  PENDING, // Lesson is pending
  CONFIRMED, // Lesson is confirmed
  COMPLETED, // Lesson has been completed
  CANCELLED // Lesson has been cancelled
}
