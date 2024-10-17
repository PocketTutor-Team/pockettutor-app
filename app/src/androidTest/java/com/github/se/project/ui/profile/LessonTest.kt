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
    val lesson = Lesson()
    assertEquals("", lesson.id)
    assertEquals("", lesson.title)
    assertEquals("", lesson.description)
    assertEquals(Subject.NONE, lesson.subject)
    assertEquals("", lesson.tutorUid)
    assertEquals("", lesson.studentUid)
    assertEquals(0.0, lesson.minPrice)
    assertEquals(0.0, lesson.maxPrice)
    assertEquals("", lesson.timeSlot)
    assertEquals(LessonStatus.PENDING, lesson.status)
    assertEquals(listOf<Language>(), lesson.languages)
  }

  @Test
  fun testLessonCustomValues() {
    val lesson =
        Lesson(
            id = "1",
            title = "Kotlin Basics",
            description = "Introduction to Kotlin",
            subject = Subject.ICC,
            tutorUid = "tutor123",
            studentUid = "student456",
            minPrice = 50.0,
            maxPrice = 100.0,
            timeSlot = "2024-10-10T10:00:00",
            status = LessonStatus.CONFIRMED,
            languages = listOf(Language.ENGLISH)
        )
    assertEquals("1", lesson.id)
    assertEquals("Kotlin Basics", lesson.title)
    assertEquals("Introduction to Kotlin", lesson.description)
    assertEquals(Subject.ICC, lesson.subject)
    assertEquals("tutor123", lesson.tutorUid)
    assertEquals("student456", lesson.studentUid)
    assertEquals(50.0, lesson.minPrice)
    assertEquals(100.0, lesson.maxPrice)
    assertEquals("2024-10-10T10:00:00", lesson.timeSlot)
    assertEquals(LessonStatus.CONFIRMED, lesson.status)
    assertEquals(listOf(Language.ENGLISH), lesson.languages)
  }

  @Test
  fun testLessonStatusValues() {
    assertNotNull(LessonStatus.valueOf("PENDING"))
    assertNotNull(LessonStatus.valueOf("CONFIRMED"))
    assertNotNull(LessonStatus.valueOf("COMPLETED"))
    assertNotNull(LessonStatus.valueOf("CANCELLED"))
  }
}
