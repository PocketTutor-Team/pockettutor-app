package com.github.se.project.ui.profile

import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class LessonTest {

  @Test
  fun testLessonDefaultValues() {
    val lesson = Lesson(latitude = 0.0, longitude = 0.0)
    assertEquals("", lesson.id)
    assertEquals("", lesson.title)
    assertEquals("", lesson.description)
    assertEquals(Subject.NONE, lesson.subject)
    assertEquals(listOf<String>(), lesson.tutorUid)
    assertEquals("", lesson.studentUid)
    assertEquals(0.0, lesson.minPrice)
    assertEquals(0.0, lesson.maxPrice)
    assertEquals("", lesson.timeSlot)
    assertEquals(LessonStatus.MATCHING, lesson.status)
    assertEquals(listOf<Language>(), lesson.languages)
    assertEquals(0.0, lesson.latitude)
    assertEquals(0.0, lesson.longitude)
  }

  @Test
  fun testLessonCustomValues() {
    val lesson =
        Lesson(
            id = "1",
            title = "Kotlin Basics",
            description = "Introduction to Kotlin",
            subject = Subject.ICC,
            tutorUid = listOf("tutor123"),
            studentUid = "student456",
            minPrice = 50.0,
            maxPrice = 100.0,
            timeSlot = "2024-10-10T10:00:00",
            status = LessonStatus.CONFIRMED,
            languages = listOf(Language.ENGLISH),
            latitude = 0.0,
            longitude = 0.0)
    assertEquals("1", lesson.id)
    assertEquals("Kotlin Basics", lesson.title)
    assertEquals("Introduction to Kotlin", lesson.description)
    assertEquals(Subject.ICC, lesson.subject)
    assertEquals(listOf("tutor123"), lesson.tutorUid)
    assertEquals("student456", lesson.studentUid)
    assertEquals(50.0, lesson.minPrice)
    assertEquals(100.0, lesson.maxPrice)
    assertEquals("2024-10-10T10:00:00", lesson.timeSlot)
    assertEquals(LessonStatus.CONFIRMED, lesson.status)
    assertEquals(listOf(Language.ENGLISH), lesson.languages)
    assertEquals(0.0, lesson.latitude)
    assertEquals(0.0, lesson.longitude)
  }

  @Test
  fun testLessonStatusValues() {
    assertNotNull(LessonStatus.valueOf("STUDENT_REQUESTED"))
    assertNotNull(LessonStatus.valueOf("PENDING_TUTOR_CONFIRMATION"))
    assertNotNull(LessonStatus.valueOf("COMPLETED"))
    assertNotNull(LessonStatus.valueOf("STUDENT_CANCELLED"))
    assertNotNull(LessonStatus.valueOf("TUTOR_CANCELLED"))
    assertNotNull(LessonStatus.valueOf("MATCHING"))
    assertNotNull(LessonStatus.valueOf("CONFIRMED"))
  }
}
