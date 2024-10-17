package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  REQUESTED,
  PENDING, // Lesson is pending
  CONFIRMED, // Lesson is confirmed
  COMPLETED, // Lesson has been completed
  CANCELLED // Lesson has been cancelled
}
