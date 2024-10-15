package com.github.se.project.model.profile

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
    private lateinit var repository: ProfilesRepositoryFirestore

    @Before
    fun setup() {
        firestore = mock()
        repository = ProfilesRepositoryFirestore(firestore)
    }

    @Test
    fun testGetNewUid() {
        val uid = repository.getNewUid()
        assertTrue (uid.isNotEmpty())
    }

    @Test
    fun testGetProfiles_Success() {
        // Arrange
        val document1 = mock<DocumentSnapshot>()
        val document2 = mock<DocumentSnapshot>()
        val querySnapshot = mock<QuerySnapshot>()

        // Set up mock behavior
        whenever(document1.id).thenReturn("1")
        whenever(document1.getString("googleUid")).thenReturn("googleUid1")
        whenever(document1.getString("firstName")).thenReturn("FirstName1")
        whenever(document1.getString("lastName")).thenReturn("LastName1")
        whenever(document1.getString("phoneNumber")).thenReturn("1234567890")
        whenever(document1.getString("role")).thenReturn("STUDENT")
        whenever(document1.getString("section")).thenReturn("SECTION_A")
        whenever(document1.getString("academicLevel")).thenReturn("LEVEL_1")
        whenever(document1.get("languages")).thenReturn(listOf("ENGLISH"))
        whenever(document1.get("subjects")).thenReturn(listOf("MATH"))
        whenever(document1.get("schedule")).thenReturn(listOf(0, 1, 2))

        whenever(document2.id).thenReturn("2")
        whenever(document2.getString("googleUid")).thenReturn("googleUid2")
        whenever(document2.getString("firstName")).thenReturn("FirstName2")
        whenever(document2.getString("lastName")).thenReturn("LastName2")
        whenever(document2.getString("phoneNumber")).thenReturn("0987654321")
        whenever(document2.getString("role")).thenReturn("TUTOR")
        whenever(document2.getString("section")).thenReturn("SECTION_B")
        whenever(document2.getString("academicLevel")).thenReturn("LEVEL_2")
        whenever(document2.get("languages")).thenReturn(listOf("SPANISH"))
        whenever(document2.get("subjects")).thenReturn(listOf("SCIENCE"))
        whenever(document2.get("schedule")).thenReturn(listOf(1, 0, 3))

        whenever(querySnapshot.documents).thenReturn(listOf(document1, document2))
        whenever(firestore.collection("profiles").get()).thenReturn(mockTaskWithResult(querySnapshot))

        // Act
        val profiles = mutableListOf<Profile>()
        repository.getProfiles(onSuccess = { profiles.addAll(it) }, onFailure = { fail("Expected success, got failure") })

        // Assert
        assertEquals(2, profiles.size)
        assertEquals("1", profiles[0].uid)
        assertEquals("2", profiles[1].uid)
    }

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


}
