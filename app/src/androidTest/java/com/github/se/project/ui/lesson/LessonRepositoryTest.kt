package com.github.se.project.ui.lesson

import com.github.se.project.model.lesson.LessonRepositoryFirestore
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LessonRepositoryFirestoreTest {
  private lateinit var db: FirebaseFirestore
  private lateinit var repository: LessonRepositoryFirestore

  @Before
  fun setUp() {
    // Mock Firestore
    db = mock(FirebaseFirestore::class.java)
    // Create repository instance with mocked Firestore
    repository = LessonRepositoryFirestore(db)
  }

  @Test
  fun documentToLesson_withValidDocument_returnsLesson() {
    // Given
    val document = mock<DocumentSnapshot>()
    val languagesList = listOf("FRENCH", "ENGLISH")

    `when`(document.id).thenReturn("testId")
    `when`(document.getString("title")).thenReturn("Test Title")
    `when`(document.getString("description")).thenReturn("Test Description")
    `when`(document.getString("subject")).thenReturn("AICC")
    `when`(document.getString("tutorUid")).thenReturn("tutor123")
    `when`(document.getString("studentUid")).thenReturn("student123")
    `when`(document.getDouble("minPrice")).thenReturn(10.0)
    `when`(document.getDouble("maxPrice")).thenReturn(20.0)
    `when`(document.getDouble("price")).thenReturn(15.0)
    `when`(document.getString("timeSlot")).thenReturn("2024-01-01")
    `when`(document.getString("status")).thenReturn("PENDING")
    `when`(document.get("languages")).thenReturn(languagesList)

    // When
    val lesson = repository.documentToLesson(document)

    // Then
    assertNotNull(lesson)
    assertEquals("testId", lesson?.id)
    assertEquals("Test Title", lesson?.title)
    assertEquals("Test Description", lesson?.description)
    assertEquals(Subject.AICC, lesson?.subject)
    assertEquals("tutor123", lesson?.tutorUid)
    assertEquals("student123", lesson?.studentUid)
    assertEquals(10.0, lesson?.minPrice)
    assertEquals(20.0, lesson?.maxPrice)
    assertEquals(15.0, lesson?.price)
    assertEquals("2024-01-01", lesson?.timeSlot)
    assertEquals(LessonStatus.PENDING, lesson?.status)
    assertEquals(listOf(Language.FRENCH, Language.ENGLISH), lesson?.languages)
  }

  @Test
  fun documentToLesson_withMissingRequiredField_returnsNull() {
    // Given
    val document = mock<DocumentSnapshot>()
    `when`(document.id).thenReturn("testId")
    `when`(document.getString("title")).thenReturn(null) // Missing required field

    // When
    val lesson = repository.documentToLesson(document)

    // Then
    assertNull(lesson)
  }

  @Test
  fun documentToLesson_withInvalidLanguage_returnsLessonWithoutInvalidLanguage() {
    // Given
    val document = mock<DocumentSnapshot>()
    val languagesList = listOf("FRENCH", "INVALID_LANGUAGE")

    // Setup all required fields
    `when`(document.id).thenReturn("testId")
    `when`(document.getString("title")).thenReturn("Test Title")
    `when`(document.getString("description")).thenReturn("Test Description")
    `when`(document.getString("subject")).thenReturn("AICC")
    `when`(document.getString("tutorUid")).thenReturn("tutor123")
    `when`(document.getString("studentUid")).thenReturn("student123")
    `when`(document.getDouble("minPrice")).thenReturn(10.0)
    `when`(document.getDouble("maxPrice")).thenReturn(20.0)
    `when`(document.getDouble("price")).thenReturn(15.0)
    `when`(document.getString("timeSlot")).thenReturn("2024-01-01")
    `when`(document.getString("status")).thenReturn("PENDING")
    `when`(document.get("languages")).thenReturn(languagesList)

    // When
    val lesson = repository.documentToLesson(document)

    // Then
    assertNotNull(lesson)
    assertEquals(listOf(Language.FRENCH), lesson?.languages)
  }
}
