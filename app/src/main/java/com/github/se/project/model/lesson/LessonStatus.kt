package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  REQUESTED,
  PENDING, // Lesson is pending => TODO: Remove it and use STUDENT_PENDING and TUTOR_PENDING
  TUTOR_PENDING, // Lesson is pending and wait for tutor to confirm
  STUDENT_PENDING, // Lesson is pending and wait for student to confirm
  CONFIRMED, // Lesson is confirmed
  COMPLETED, // Lesson has been completed
  CANCELLED // Lesson has been cancelled
}
