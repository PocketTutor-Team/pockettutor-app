package com.github.se.project.ui.profile

import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonRepositoryFirestore
import com.github.se.project.model.profile.Role
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.util.EnumSet

class ProfilesRepositoryFirestoreTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: LessonRepositoryFirestore

    @Before
    fun setup() {
        firestore = mock()
        repository = LessonRepositoryFirestore(firestore)
    }
/*
    @Test
    fun testGetLessons_Success() {
        // Arrange
        val document1 = mock<DocumentSnapshot>()
        val document2 = mock<DocumentSnapshot>()
        val querySnapshot = mock<QuerySnapshot>()

        // Set up mock behavior
        whenever(document1.id).thenReturn("1")
        whenever(document1.getString("title")).thenReturn("title1")
        whenever(document1.getString("description")).thenReturn("description1")
        whenever(document1.getString("tutorUid")).thenReturn("tutorUid1")
        whenever(document1.getString("studentUid")).thenReturn("studentUid1")
        whenever(document1.getDouble("price")).thenReturn(1.0)
        whenever(document1.getString("timeslot")).thenReturn("2024-10-10T10:45:00")
        whenever(document1.getString("status")).thenReturn("PENDING")
        whenever(document1.getString("language")).thenReturn("Frnech")

        whenever(document2.id).thenReturn("2")
        whenever(document1.getString("title")).thenReturn("title2")
        whenever(document1.getString("description")).thenReturn("description2")
        whenever(document1.getString("tutorUid")).thenReturn("tutorUid2")
        whenever(document1.getString("studentUid")).thenReturn("studentUid2")
        whenever(document1.getDouble("price")).thenReturn(2.0)
        whenever(document1.getString("timeslot")).thenReturn("2024-10-10T10:47:00")
        whenever(document1.getString("status")).thenReturn("CONFIRMED")
        whenever(document1.getString("language")).thenReturn("Emglnish")

        whenever(querySnapshot.documents).thenReturn(listOf(document1, document2))

        whenever(firestore.collection("lessons").get()).thenReturn(mockTaskWithResult(querySnapshot))

        // Act
        val lessons = mutableListOf<Lesson>()
        repository.getLessonsByUserIdAndRole(Role.TUTOR, "tutorUid1", onSuccess = { lessons.addAll(it) }, onFailure = { fail("Expected success, got failure") })

        // Assert
        assertEquals(1, lessons.size)
        assertEquals("1", lessons[0].id)
    }
/*
    @Test
    fun testGetProfiles_Failure() {
        // Arrange
        val exception = Exception("Firestore error")
        whenever(firestore.collection("profiles").get()).thenReturn(mockTaskWithFailure(exception))

        // Act
        var isFailureCalled = false
        repository.getProfiles(onSuccess = { fail("Expected failure, got success") }) {
            isFailureCalled = true
            assertEquals("Firestore error", it.message)
        }

        // Assert
        assertTrue (isFailureCalled)
    }

    @Test
    fun testAddProfile_Success() {
        // Arrange
        val profile = Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last",
            phoneNumber = "1234567890", role = Role.STUDENT, section = Section.GM,
            academicLevel = AcademicLevel.MA2, languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(TutoringSubject::class.java), schedule = listOf())

        whenever(firestore.collection("profiles").document(profile.uid).set(any())).thenReturn(mockTaskWithSuccess())

        // Act
        var isSuccessCalled = false
        repository.addProfile(profile, onSuccess = { isSuccessCalled = true }, onFailure = { fail("Expected success, got failure") })

        // Assert
        assertTrue (isSuccessCalled )
    }

    @Test
    fun testDeleteProfileById_Success() {
        // Arrange
        val uidToDelete = "1"
        whenever(firestore.collection("profiles").document(uidToDelete).delete()).thenReturn(mockTaskWithSuccess())

        // Act
        var isSuccessCalled = false
        repository.deleteProfileById(uidToDelete, onSuccess = { isSuccessCalled = true }, onFailure = { fail("Expected success, got failure") })

        // Assert
        assertTrue (isSuccessCalled)
    }
*/
    private fun <T> mockTaskWithResult(result: T): Task<T> {
        val task = mock<Task<T>>()
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(result)
        return task
    }

    private fun mockTaskWithFailure(exception: Exception): Task<QuerySnapshot> {
        val task = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
        whenever(task.isSuccessful).thenReturn(false)
        whenever(task.exception).thenReturn(exception)
        return task
    }

    private fun mockTaskWithSuccess(): Task<Void> {
        val task = mock<Task<Void>>()
        whenever(task.isSuccessful).thenReturn(true)
        return task
    }

*/
}