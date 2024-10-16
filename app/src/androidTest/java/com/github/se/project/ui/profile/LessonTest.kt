package com.github.se.project.ui.profile

import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonStatus
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
        assertEquals("", lesson.tutorUid)
        assertEquals("", lesson.studentUid)
        assertEquals(0.0, lesson.price)
        assertEquals("", lesson.timeSlot)
        assertEquals(LessonStatus.PENDING, lesson.status)
        assertEquals("", lesson.language)
    }

    @Test
    fun testLessonCustomValues() {
        val lesson = Lesson(
            id = "1",
            title = "Kotlin Basics",
            description = "Introduction to Kotlin",
            tutorUid = "tutor123",
            studentUid = "student456",
            price = 50.0,
            timeSlot = "2024-10-10T10:00:00",
            status = LessonStatus.CONFIRMED,
            language = "EN"
        )
        assertEquals("1", lesson.id)
        assertEquals("Kotlin Basics", lesson.title)
        assertEquals("Introduction to Kotlin", lesson.description)
        assertEquals("tutor123", lesson.tutorUid)
        assertEquals("student456", lesson.studentUid)
        assertEquals(50.0, lesson.price)
        assertEquals("2024-10-10T10:00:00", lesson.timeSlot)
        assertEquals(LessonStatus.CONFIRMED, lesson.status)
        assertEquals("EN", lesson.language)
    }

    @Test
    fun testLessonStatusValues() {
        assertNotNull(LessonStatus.valueOf("PENDING"))
        assertNotNull(LessonStatus.valueOf("CONFIRMED"))
        assertNotNull(LessonStatus.valueOf("COMPLETED"))
        assertNotNull(LessonStatus.valueOf("CANCELLED"))
    }
}