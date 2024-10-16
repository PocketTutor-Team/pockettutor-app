package com.android.sample.model.lesson

data class Lesson(
    val id: String = "", // Unique identifier for the lesson
    val title: String = "", // Title of the lesson
    val description: String = "", // Description of the lesson
    val tutorUid: String = "", // User ID of the tutor
    val studentUid: String = "", // User ID of the student (if booked)
    val price: Double = 0.0, // Price for the lesson
    val timeSlot: String, // Date and time of the lesson
    val status: LessonStatus = LessonStatus.REQUESTED, // Status of the lesson
    val language: String = "" // todo: create a language enum ?
)

// Enum class to define lesson status
enum class LessonStatus {
  REQUESTED, // Lesson has been requested by the student
  PENDING, // Lesson is pending waiting for confirmation of the tutor
  SCHEDULED, // // Lesson is planned and confirmed by the tutor or student
  COMPLETED, // Lesson has been completed
}
