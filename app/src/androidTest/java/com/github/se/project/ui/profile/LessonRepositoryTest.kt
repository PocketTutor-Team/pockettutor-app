package com.github.se.project.ui.profile

import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonRepositoryFirestore
import com.android.sample.model.lesson.LessonStatus
import com.github.se.project.LessonRepository
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.mockito.Mockito.mock

class LessonRepositoryTest {

    private lateinit var repository: LessonRepository
    private val userUid = "test tutorUid"
    private val lesson = Lesson(
        id = "1",
        title = "Kotlin Basics",
        description = "Introduction to Kotlin",
        tutorUid = "test tutorUid",
        studentUid = "test studentUid",
        price = 50.0,
        timeSlot = "2024-10-10T10:00:00",
        status = LessonStatus.CONFIRMED,
        language = "EN"
    )

    @Test
    fun testInitSuccess() {
        val db = mock(FirebaseFirestore::class.java)
        val repository = LessonRepositoryFirestore(db)

        var successCalled = false
        repository.init { successCalled = true }

        assert(successCalled)
    }
}