package com.github.se.project.model.lesson

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.github.se.project.model.profile.*
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class SuitabilityScoreCalculatorTest {

  // Base Profiles and Lessons
  private val baseTutorProfile =
      Profile(
          uid = "tutor123",
          token = "",
          googleUid = "googleUid123",
          firstName = "Tutor",
          lastName = "Example",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.PhD,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS),
          schedule = createAvailableSchedule(),
          price = 30)

  private val baseStudentProfile =
      Profile(
          uid = "student456",
          token = "",
          googleUid = "googleUid456",
          firstName = "Student",
          lastName = "Example",
          phoneNumber = "0987654321",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA3,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS))

  private val baseLesson =
      Lesson(
          id = "lesson123",
          title = "Analysis Lesson",
          description = "Calculus and Differential Equations",
          subject = Subject.ANALYSIS,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf(),
          studentUid = "student456",
          minPrice = 20.0,
          maxPrice = 50.0,
          price = 0.0,
          timeSlot = "30/11/2024T10:00:00",
          status = LessonStatus.MATCHING,
          latitude = 40.0,
          longitude = -74.0)

  // Helper Functions for Schedules
  private fun createAvailableSchedule(): List<List<Int>> {
    return List(7) { List(12) { 1 } }
  }

  private fun createUnavailableSchedule(): List<List<Int>> {
    return List(7) { List(12) { 0 } }
  }

  // Tests
  @Test
  fun testCalculateSuitabilityScore() {
    val lesson = baseLesson.copy()
    val tutorProfile = baseTutorProfile.copy()
    val studentProfile = baseStudentProfile.copy()
    val tutorLocation = LatLng(40.0, -74.0) // approximate NYC location

    val score =
        SuitabilityScoreCalculator.calculateSuitabilityScore(
            lesson, tutorLocation, tutorProfile, studentProfile)

    assertEquals(100, score)
  }

  @Test
  fun testCalculateSuitabilityScore_WithDifferentParameters() {
    val lesson =
        baseLesson.copy(
            id = "lesson124",
            title = "Physics Lesson",
            description = "Classical Mechanics",
            subject = Subject.PHYSICS,
            languages = listOf(Language.FRENCH),
            minPrice = 20.0,
            maxPrice = 40.0,
            timeSlot = "26/11/2024T14:00:00",
            latitude = 40.7128,
            longitude = -74.0060)

    val tutorProfile =
        baseTutorProfile.copy(
            academicLevel = AcademicLevel.BA2,
            subjects = listOf(Subject.ANALYSIS),
            schedule = createUnavailableSchedule(),
            price = 50)

    val studentProfile =
        baseStudentProfile.copy(
            languages = listOf(Language.FRENCH), subjects = listOf(Subject.PHYSICS))

    val tutorLocation = LatLng(41.730610, -73.935242)

    val score =
        SuitabilityScoreCalculator.calculateSuitabilityScore(
            lesson, tutorLocation, tutorProfile, studentProfile)

    assertEquals(true, score < 50)
  }

  @Test
  fun testComputeSubjectMatch() {
    val tutorProfile =
        baseTutorProfile.copy(
            academicLevel = AcademicLevel.MA2, subjects = listOf(Subject.ANALYSIS, Subject.PHYSICS))

    val lesson = baseLesson.copy(subject = Subject.ANALYSIS)

    val resultMatch = SuitabilityScoreCalculator.computeSubjectMatch(lesson, tutorProfile)
    assertEquals(true, resultMatch)

    val newLesson = lesson.copy(subject = Subject.ICC)
    val resultNoMatch = SuitabilityScoreCalculator.computeSubjectMatch(newLesson, tutorProfile)
    assertEquals(false, resultNoMatch)
  }

  @Test
  fun testComputeLanguageMatch() {
    val tutorProfile =
        baseTutorProfile.copy(
            academicLevel = AcademicLevel.MA2,
            languages = listOf(Language.ENGLISH, Language.FRENCH))

    val lesson = baseLesson.copy(languages = listOf(Language.ENGLISH))

    val resultMatch = SuitabilityScoreCalculator.computeLanguageMatch(lesson, tutorProfile)
    assertEquals(true, resultMatch)

    val newLesson = lesson.copy(languages = listOf(Language.GERMAN))
    val resultNoMatch = SuitabilityScoreCalculator.computeLanguageMatch(newLesson, tutorProfile)
    assertEquals(false, resultNoMatch)
  }

  @Test
  fun testGetColorForScore() {
    val colorLow = SuitabilityScoreCalculator.getColorForScore(25, false)
    val colorMid = SuitabilityScoreCalculator.getColorForScore(50, false)
    val colorHigh = SuitabilityScoreCalculator.getColorForScore(100, false)

    val expectedColorLow =
        lerp(Color(0xFFFF0000), Color(0xFFFFA500), (25 / 100f).coerceIn(0f, 1f) * 2)
    val expectedColorMid = Color(0xFFFFA500)
    val expectedColorHigh = Color(0xFF009F00)

    assertColorsApproximatelyEqual(expectedColorLow, colorLow)
    assertColorsApproximatelyEqual(expectedColorMid, colorMid)
    assertColorsApproximatelyEqual(expectedColorHigh, colorHigh)

    val colorHighDark = SuitabilityScoreCalculator.getColorForScore(100, true)
    val expectedColorHighDark = Color(0xFF00FF00)
    assertColorsApproximatelyEqual(expectedColorHighDark, colorHighDark)
  }

  // helper function to compare colors with a tolerance
  private fun assertColorsApproximatelyEqual(
      expected: Color,
      actual: Color,
      tolerance: Float = 0.01f
  ) {
    assertEquals(expected.red, actual.red, tolerance)
    assertEquals(expected.green, actual.green, tolerance)
    assertEquals(expected.blue, actual.blue, tolerance)
    assertEquals(expected.alpha, actual.alpha, tolerance)
  }
}
