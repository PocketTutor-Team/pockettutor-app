package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  REQUESTED, // Lesson has been requested => TODO: remove this (replaced by STUDENT_REQUESTED and
  // TUTOR_REQUESTED)
  STUDENT_REQUESTED, // Lesson has been requested by student => need to be confirmed by the tutor
  TUTOR_REQUESTED, // Lesson has been requested by tutor => need to be confirmed by the student
  PENDING, // Lesson is pending => need to find a tutor
  CONFIRMED, // Lesson is confirmed
  COMPLETED, // Lesson has been completed
  CANCELLED // Lesson has been cancelled
}
