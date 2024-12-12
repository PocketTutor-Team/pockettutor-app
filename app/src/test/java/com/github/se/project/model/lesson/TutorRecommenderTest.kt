package com.github.se.project.model.lesson

import com.github.se.project.model.profile.*
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class TutorRecommenderTest {

  private val studentProfile =
      Profile(
          uid = "student1",
          token = "",
          googleUid = "g_student1",
          firstName = "Alice",
          lastName = "Student",
          phoneNumber = "0000000000",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA1,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS))

  private val tutorProfileHighLevelMatchingSection =
      Profile(
          uid = "tutor1",
          token = "",
          googleUid = "g_tutor1",
          firstName = "Bob",
          lastName = "Tutor",
          phoneNumber = "1111111111",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.PhD,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS))

  private val tutorProfileMidLevelMatchingSection =
      Profile(
          uid = "tutor2",
          token = "",
          googleUid = "g_tutor2",
          firstName = "Charlie",
          lastName = "Tutor",
          phoneNumber = "2222222222",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS))

  private val tutorProfileLowLevelDifferentSection =
      Profile(
          uid = "tutor3",
          token = "",
          googleUid = "g_tutor3",
          firstName = "Diana",
          lastName = "Tutor",
          phoneNumber = "3333333333",
          role = Role.TUTOR,
          section = Section.MA, // Different from IN
          academicLevel = AcademicLevel.BA1,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS))

  private val completedLessonWithHighRating =
      Lesson(
          id = "lesson_completed_high",
          title = "Completed High Rating",
          description = "A completed lesson with high rating",
          subject = Subject.ANALYSIS,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf(),
          studentUid = "student1",
          minPrice = 0.0,
          maxPrice = 0.0,
          price = 0.0,
          timeSlot = "10/12/2024T10:00:00",
          status = LessonStatus.COMPLETED,
          latitude = 0.0,
          longitude = 0.0,
          rating =
              LessonRating(
                  grade = 5, comment = "Excellent lesson", date = Timestamp.now(), canEdit = false))

  private val completedLessonWithMidRating =
      Lesson(
          id = "lesson_completed_mid",
          title = "Completed Mid Rating",
          description = "A completed lesson with mid rating",
          subject = Subject.ANALYSIS,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf(),
          studentUid = "student1",
          minPrice = 0.0,
          maxPrice = 0.0,
          price = 0.0,
          timeSlot = "11/12/2024T10:00:00",
          status = LessonStatus.COMPLETED,
          latitude = 0.0,
          longitude = 0.0,
          rating =
              LessonRating(
                  grade = 3, comment = "Average lesson", date = Timestamp.now(), canEdit = false))

  private val completedLessonWithoutRating =
      Lesson(
          id = "lesson_completed_no_rating",
          title = "Completed No Rating",
          description = "A completed lesson without rating",
          subject = Subject.ANALYSIS,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf(),
          studentUid = "student1",
          minPrice = 0.0,
          maxPrice = 0.0,
          price = 0.0,
          timeSlot = "12/12/2024T10:00:00",
          status = LessonStatus.COMPLETED,
          latitude = 0.0,
          longitude = 0.0,
          rating = null)

  // Mock the lessonViewModel and control its currentUserLessons
  private val mockLessonViewModel = mock(LessonViewModel::class.java)
  private val lessonsFlow = MutableStateFlow<List<Lesson>>(emptyList())

  @Before
  fun setUp() {
    // Whenever currentUserLessons is called, return our flow
    whenever(mockLessonViewModel.currentUserLessons).thenReturn(lessonsFlow)
  }

  @Test
  fun testRecommendTutorsForLesson_NoRatedLessons() {
    // No completed lessons
    lessonsFlow.value = emptyList()

    val tutors =
        listOf(
            tutorProfileHighLevelMatchingSection,
            tutorProfileMidLevelMatchingSection,
            tutorProfileLowLevelDifferentSection)

    val recommended =
        TutorRecommender.recommendTutorsForLesson(studentProfile, tutors, mockLessonViewModel)

    assertEquals(
        listOf(
            tutorProfileHighLevelMatchingSection,
            tutorProfileMidLevelMatchingSection,
            tutorProfileLowLevelDifferentSection),
        recommended)
  }

  @Test
  fun testRecommendTutorsForLesson_WithRatedLessons() {
    // With rated lessons: average = (5 + 3)/2 = 4.0/5 = 0.8 rating
    lessonsFlow.value = listOf(completedLessonWithHighRating, completedLessonWithMidRating)

    val tutors =
        listOf(
            tutorProfileHighLevelMatchingSection,
            tutorProfileMidLevelMatchingSection,
            tutorProfileLowLevelDifferentSection)

    val recommended =
        TutorRecommender.recommendTutorsForLesson(studentProfile, tutors, mockLessonViewModel)

    assertEquals(
        listOf(
            tutorProfileHighLevelMatchingSection,
            tutorProfileMidLevelMatchingSection,
            tutorProfileLowLevelDifferentSection),
        recommended)
  }

  @Test
  fun testRecommendTutorsForLesson_WithMixedFactors() {
    // rating=0.8 from previous calculation
    lessonsFlow.value = listOf(completedLessonWithHighRating, completedLessonWithMidRating)

    val tutorHighLevelDiffSection = tutorProfileHighLevelMatchingSection.copy(section = Section.MA)

    val tutors =
        listOf(
            tutorProfileMidLevelMatchingSection, // MA2, IN
            tutorHighLevelDiffSection, // PhD, MA
            tutorProfileLowLevelDifferentSection // BA1, MA
            )

    val recommended =
        TutorRecommender.recommendTutorsForLesson(studentProfile, tutors, mockLessonViewModel)

    assertEquals(
        listOf(
            tutorProfileMidLevelMatchingSection,
            tutorHighLevelDiffSection,
            tutorProfileLowLevelDifferentSection),
        recommended)
  }

  @Test
  fun testRecommendTutorsForLesson_WithNoSectionMatchAndNoRating() {
    // No rating -> rating=1.0, no section matches
    lessonsFlow.value = emptyList()

    val tutorHighLevelOut = tutorProfileHighLevelMatchingSection.copy(section = Section.MA)
    val tutorMidLevelOut = tutorProfileMidLevelMatchingSection.copy(section = Section.MA)
    val tutorLowLevelOut = tutorProfileLowLevelDifferentSection // already MA

    val tutors = listOf(tutorLowLevelOut, tutorHighLevelOut, tutorMidLevelOut)

    val recommended =
        TutorRecommender.recommendTutorsForLesson(studentProfile, tutors, mockLessonViewModel)

    assertEquals(listOf(tutorHighLevelOut, tutorMidLevelOut, tutorLowLevelOut), recommended)
  }
}
