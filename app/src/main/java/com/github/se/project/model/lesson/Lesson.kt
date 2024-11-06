package com.github.se.project.model.lesson

import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject

data class Lesson(
    var id: String = "", // Unique identifier for the lesson
    val title: String = "", // Title of the lesson
    val description: String = "", // Description of the lesson
    val subject: Subject = Subject.NONE,
    val languages: List<Language> = listOf(), // Languages spoken in the lesson
    val tutorUid: List<String> = listOf(), // User ID of the tutor
    val studentUid: String = "", // User ID of the student (if booked)
    val minPrice: Double = 0.0, // Price for the lesson
    val maxPrice: Double = 0.0, // Price for the lesson
    val price: Double = 0.0, // Price for the lesson
    val timeSlot: String = "", // Time slot for the lesson (e.g., "30/10/2024T10:00:00")
    val status: LessonStatus = LessonStatus.STUDENT_REQUESTED, // Status of the lesson
)
