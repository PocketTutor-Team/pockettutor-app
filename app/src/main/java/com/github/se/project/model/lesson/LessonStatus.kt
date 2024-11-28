package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  STUDENT_REQUESTED, // Lesson has been requested by student => need to be confirmed by the tutor
  PENDING_TUTOR_CONFIRMATION,
  CONFIRMED, // Lesson has been confirmed by both student and tutor
  COMPLETED,
  STUDENT_CANCELLED, // Lesson has been canceled by student
  TUTOR_CANCELLED, // Lesson has been canceled by tutor
  MATCHING, // Matching is processed
  INSTANT_REQUESTED, // Instant lesson requested by student
  INSTANT_CONFIRMED, // Instant lesson confirmed by tutor
}
