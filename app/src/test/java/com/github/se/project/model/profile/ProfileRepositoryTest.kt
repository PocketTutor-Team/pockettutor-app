package com.github.se.project.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.EnumSet
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockDocumentReferenceTutor: DocumentReference
  @Mock private lateinit var mockDocumentReferenceStudent: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockToDoQuerySnapshot: QuerySnapshot

  private lateinit var profileRepositoryFirestore: ProfilesRepositoryFirestore

  private val tutorProfile =
      Profile(
          uid = "1",
          googleUid = "1",
          firstName = "Tutor",
          lastName = "Tutor",
          phoneNumber = "123456789",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA1,
          languages = EnumSet.of(Language.ENGLISH),
          subjects = EnumSet.of(TutoringSubject.ALGEBRA),
          schedule = List(7) { List(12) { 1 } },
          price = 20)

  private val studentProfile =
      Profile(
          uid = "2",
          googleUid = "2",
          firstName = "Student",
          lastName = "Student",
          phoneNumber = "987654321",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA1)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    profileRepositoryFirestore = ProfilesRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document("1")).thenReturn(mockDocumentReferenceTutor)
    `when`(mockCollectionReference.document("2")).thenReturn(mockDocumentReferenceStudent)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getNewUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = profileRepositoryFirestore.getNewUid()
    assert(uid == "1")
  }

  /**
   * This test verifies that when fetching a Profile list, the Firestore `get()` is called on the
   * collection reference and not the document reference.
   */
  @Test
  fun getProfiles_callsDocuments() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockToDoQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockToDoQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    profileRepositoryFirestore.getProfiles(
        onSuccess = {
          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the 'documents' field was accessed
    verify(timeout(100)) { (mockToDoQuerySnapshot).documents }
  }

  /**
   * This test verifies that when we add a new Tutor Profile, the Firestore `set()` is called on the
   * document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun addTutorProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReferenceTutor.set(any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    profileRepositoryFirestore.addProfile(tutorProfile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure Firestore collection method was called to reference the "Profiles" collection
    verify(mockDocumentReferenceTutor).set(any())
  }

  /**
   * This test verifies that when we add a new Student Profile, the Firestore `set()` is called on
   * the document reference. This does NOT CHECK the actual data being added
   */
  @Test
  fun addStudentProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReferenceStudent.set(any()))
        .thenReturn(Tasks.forResult(null)) // Simulate success

    profileRepositoryFirestore.addProfile(studentProfile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    // Ensure Firestore collection method was called to reference the "Profiles" collection
    verify(mockDocumentReferenceStudent).set(any())
  }

  /**
   * This check that the correct Firestore method is called when deleting. Does NOT CHECK that the
   * correct data is deleted.
   */
  @Test
  fun deleteProfileByTutorId_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReferenceTutor.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById(tutorProfile.uid, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReferenceTutor).delete()
  }

  /**
   * This check that the correct Firestore method is called when deleting. Does NOT CHECK that the
   * correct data is deleted.
   */
  @Test
  fun deleteProfileByStudentId_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReferenceStudent.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById(studentProfile.uid, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReferenceStudent).delete()
  }

  /**
   * This test verifies that when updating a Tutor Profile, the Firestore `set()` is called on the
   * document reference. This does NOT CHECK the actual data being updated
   */
  @Test
  fun updateTutorProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReferenceTutor.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(tutorProfile, {}, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReferenceTutor).set(any())
  }

  /**
   * This test verifies that when updating a Student Profile, the Firestore `set()` is called on the
   * document reference. This does NOT CHECK the actual data being updated
   */
  @Test
  fun updateStudentProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReferenceStudent.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(studentProfile, {}, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReferenceStudent).set(any())
  }

  @Test
  fun addTutorProfile_callsFailure() {
    `when`(mockDocumentReferenceTutor.set(any())).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.addProfile(
        tutorProfile, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun addStudentProfile_callsFailure() {
    `when`(mockDocumentReferenceStudent.set(any())).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.addProfile(
        studentProfile, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updateTutorProfile_callsFailure() {
    `when`(mockDocumentReferenceTutor.set(any())).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.updateProfile(
        tutorProfile, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun updateStudentProfile_callsFailure() {
    `when`(mockDocumentReferenceStudent.set(any())).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.updateProfile(
        studentProfile, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun deleteTutorProfile_callsFailure() {
    `when`(mockDocumentReferenceTutor.delete()).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.deleteProfileById(
        tutorProfile.uid, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun deleteStudentProfile_callsFailure() {
    `when`(mockDocumentReferenceStudent.delete()).thenReturn(Tasks.forException(Exception()))

    profileRepositoryFirestore.deleteProfileById(
        studentProfile.uid, { fail("Success callback should not be called") }, { assert(true) })

    shadowOf(Looper.getMainLooper()).idle()
  }
}
