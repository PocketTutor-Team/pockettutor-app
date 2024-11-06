package com.github.se.project.model.lesson

// Enum class to define lesson status
enum class LessonStatus {
  STUDENT_REQUESTED, // Lesson has been requested by student => need to be confirmed by the tutor
  PENDING_STUDENT_CONFIRMATION, // Lesson has been requested by tutor and confirmed by student => need to be confirmed by the student
  CONFIRMED, // Lesson has been confirmed by both student and tutor
  COMPLETED
}
