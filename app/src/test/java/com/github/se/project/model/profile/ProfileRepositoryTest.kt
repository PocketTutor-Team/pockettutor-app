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
          token = "",
          googleUid = "1",
          firstName = "Tutor",
          lastName = "Tutor",
          phoneNumber = "123456789",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA1,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ALGEBRA),
          schedule = List(7) { List(12) { 1 } },
          price = 20)

  private val studentProfile =
      Profile(
          uid = "2",
          token = "",
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

  @Test
  fun profileToMap_returnCorrectTutorMap() {
    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("profileToMap", Profile::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    val profileAsMap = method.invoke(profileRepositoryFirestore, tutorProfile) as Map<String, Any>

    assert(profileAsMap["uid"] == tutorProfile.uid)
    assert(profileAsMap["googleUid"] == tutorProfile.googleUid)
    assert(profileAsMap["firstName"] == tutorProfile.firstName)
    assert(profileAsMap["lastName"] == tutorProfile.lastName)
    assert(profileAsMap["phoneNumber"] == tutorProfile.phoneNumber)
    assert(profileAsMap["role"] == tutorProfile.role.toString())
    assert(profileAsMap["section"] == tutorProfile.section.toString())
    assert(profileAsMap["academicLevel"] == tutorProfile.academicLevel.toString())
    assert(profileAsMap["languages"] == tutorProfile.languages.map { it.toString() })
    assert(profileAsMap["subjects"] == tutorProfile.subjects.map { it.toString() })
    assert(profileAsMap["schedule"] == tutorProfile.schedule.flatten())
    assert(profileAsMap["price"] == tutorProfile.price)
  }

  @Test
  fun profileToMap_returnCorrectStudentMap() {
    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("profileToMap", Profile::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    val profileAsMap = method.invoke(profileRepositoryFirestore, studentProfile) as Map<String, Any>

    assert(profileAsMap["uid"] == studentProfile.uid)
    assert(profileAsMap["googleUid"] == studentProfile.googleUid)
    assert(profileAsMap["firstName"] == studentProfile.firstName)
    assert(profileAsMap["lastName"] == studentProfile.lastName)
    assert(profileAsMap["phoneNumber"] == studentProfile.phoneNumber)
    assert(profileAsMap["role"] == studentProfile.role.toString())
    assert(profileAsMap["section"] == studentProfile.section.toString())
    assert(profileAsMap["academicLevel"] == studentProfile.academicLevel.toString())
    assert(profileAsMap["languages"] == studentProfile.languages.map { it.toString() })
    assert(profileAsMap["subjects"] == studentProfile.subjects.map { it.toString() })
    assert(profileAsMap["schedule"] == studentProfile.schedule.flatten())
    assert(profileAsMap["price"] == studentProfile.price)
  }

  @Test
  fun documentToProfile_returnCorrectTutorProfile() {
    `when`(mockDocumentSnapshot.id).thenReturn(tutorProfile.uid)
    `when`(mockDocumentSnapshot.getString("token")).thenReturn(tutorProfile.token)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(tutorProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(tutorProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(tutorProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(tutorProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(tutorProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(tutorProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(tutorProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages"))
        .thenReturn(tutorProfile.languages.map { it.toString() })
    `when`(mockDocumentSnapshot.get("subjects"))
        .thenReturn(tutorProfile.subjects.map { it.toString() })
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(tutorProfile.schedule.flatten())
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(tutorProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile

    assert(profile.uid == tutorProfile.uid)
    assert(profile.token == tutorProfile.token)
    assert(profile.googleUid == tutorProfile.googleUid)
    assert(profile.firstName == tutorProfile.firstName)
    assert(profile.lastName == tutorProfile.lastName)
    assert(profile.phoneNumber == tutorProfile.phoneNumber)
    assert(profile.role == tutorProfile.role)
    assert(profile.section == tutorProfile.section)
    assert(profile.academicLevel == tutorProfile.academicLevel)
    assert(profile.languages == tutorProfile.languages)
    assert(profile.subjects == tutorProfile.subjects)
    assert(profile.schedule == tutorProfile.schedule)
    assert(profile.price == tutorProfile.price)
  }

  @Test
  fun documentToProfile_returnCorrectStudentProfile() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("token")).thenReturn(tutorProfile.token)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(null)

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile

    assert(profile.uid == studentProfile.uid)
    assert(profile.token == tutorProfile.token)
    assert(profile.googleUid == studentProfile.googleUid)
    assert(profile.firstName == studentProfile.firstName)
    assert(profile.lastName == studentProfile.lastName)
    assert(profile.phoneNumber == studentProfile.phoneNumber)
    assert(profile.role == studentProfile.role)
    assert(profile.section == studentProfile.section)
    assert(profile.academicLevel == studentProfile.academicLevel)
    assert(profile.languages == studentProfile.languages)
    assert(profile.subjects == studentProfile.subjects)
    assert(profile.schedule == studentProfile.schedule)
    assert(profile.price == studentProfile.price)
  }

  @Test
  fun documentToProfile_NoUid_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoGoogleUid_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoFirstName_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoLastName_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoPhoneNb_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoRole_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_FalseRole_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn("ANYONE")
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoSection_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(null)
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_FalseSection_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn("ANYWHERE")
    `when`(mockDocumentSnapshot.getString("academicLevel"))
        .thenReturn(studentProfile.academicLevel.toString())
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_NoLevel_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }

  @Test
  fun documentToProfile_FalseLevel_throwException() {
    `when`(mockDocumentSnapshot.id).thenReturn(studentProfile.uid)
    `when`(mockDocumentSnapshot.getString("googleUid")).thenReturn(studentProfile.googleUid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(studentProfile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(studentProfile.lastName)
    `when`(mockDocumentSnapshot.getString("phoneNumber")).thenReturn(studentProfile.phoneNumber)
    `when`(mockDocumentSnapshot.getString("role")).thenReturn(studentProfile.role.toString())
    `when`(mockDocumentSnapshot.getString("section")).thenReturn(studentProfile.section.toString())
    `when`(mockDocumentSnapshot.getString("academicLevel")).thenReturn("SOMETHING")
    `when`(mockDocumentSnapshot.get("languages")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("subjects")).thenReturn(null)
    `when`(mockDocumentSnapshot.get("schedule")).thenReturn(null)
    `when`(mockDocumentSnapshot.getLong("price")).thenReturn(studentProfile.price.toLong())

    // Use reflection to access the private method
    val method =
        ProfilesRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToProfile", DocumentSnapshot::class.java)
    method.isAccessible = true

    // Invoke the method and get the result
    try {
      val profile = method.invoke(profileRepositoryFirestore, mockDocumentSnapshot) as Profile
      fail("Should throw exception")
    } catch (e: Exception) {
      assert(true)
    }
  }
}
