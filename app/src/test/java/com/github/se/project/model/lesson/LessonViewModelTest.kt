package com.github.se.project.model.lesson

import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq

class LessonViewModelTest {
  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private val lesson =
      Lesson(
          id = "1",
          title = "Physics Tutoring",
          description = "Mechanics and Thermodynamics",
          subject = Subject.PHYSICS,
          languages = listOf(Language.ENGLISH),
          tutorUid = "tutor123",
          studentUid = "student123",
          minPrice = 20.0,
          maxPrice = 40.0,
          timeSlot = "2024-10-10T10:00:00",
          status = LessonStatus.PENDING,
            latitude = 0.0,
            longitude = 0.0)

  @Before
  fun setUp() {
    lessonRepository = mock(LessonRepository::class.java)
    lessonViewModel = LessonViewModel(lessonRepository)
  }

  @Test
  fun getNewUidCallsRepository() {
    `when`(lessonRepository.getNewUid()).thenReturn("newUid")
    assertThat(lessonViewModel.getNewUid(), `is`("newUid"))
  }

  @Test
  fun addLessonCallsRepository() {
    lessonViewModel.addLesson(lesson) {}
    verify(lessonRepository).addLesson(eq(lesson), any(), any())
  }

  @Test
  fun deleteLessonCallsRepository() {
    lessonViewModel.deleteLesson(lesson.id) {}
    verify(lessonRepository).deleteLesson(eq(lesson.id), any(), any())
  }

  @Test
  fun selectLessonUpdatesSelectedLesson() {
    lessonViewModel.selectLesson(lesson)
    assertThat(lessonViewModel.selectedLesson.value, `is`(lesson))
  }

  @Test
  fun getLessonsForTutorCallsRepository() {
    lessonViewModel.getLessonsForTutor("tutor123") {}
    verify(lessonRepository).getLessonsForTutor(eq("tutor123"), any(), any())
  }

  @Test
  fun getLessonsForStudentCallsRepository() {
    lessonViewModel.getLessonsForStudent("student123") {}
    verify(lessonRepository).getLessonsForStudent(eq("student123"), any(), any())
  }

  @Test
  fun getLessonsForTutorUpdatesStateFlow() = runBlocking {
    val lessons = listOf(lesson)
    val onSuccessCaptor = argumentCaptor<(List<Lesson>) -> Unit>()

    lessonViewModel.getLessonsForTutor("tutor123") {}
    verify(lessonRepository).getLessonsForTutor(eq("tutor123"), onSuccessCaptor.capture(), any())
    onSuccessCaptor.firstValue.invoke(lessons)

    val collectedLessons = lessonViewModel.currentUserLessons.value
    assertThat(collectedLessons, `is`(lessons))
  }

  @Test
  fun getLessonsForStudentUpdatesStateFlow() = runBlocking {
    val lessons = listOf(lesson)
    val onSuccessCaptor = argumentCaptor<(List<Lesson>) -> Unit>()

    lessonViewModel.getLessonsForStudent("student123") {}
    verify(lessonRepository)
        .getLessonsForStudent(eq("student123"), onSuccessCaptor.capture(), any())
    onSuccessCaptor.firstValue.invoke(lessons)

    val collectedLessons = lessonViewModel.currentUserLessons.value
    assertThat(collectedLessons, `is`(lessons))
  }
}
