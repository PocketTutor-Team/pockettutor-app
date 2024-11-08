package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  STUDENT_REQUESTED, // Lesson has been requested by student => need to be confirmed by the tutor
  PENDING_TUTOR_CONFIRMATION,
  CONFIRMED, // Lesson has been confirmed by both student and tutor
  COMPLETED,
    CANCELED // Lesson has been canceled by either student or tutor
}
