import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonRepositoryFirestore
import com.github.se.project.model.profile.TutoringSubject
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LessonRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var lessonRepositoryFirestore: LessonRepositoryFirestore

  private val lesson =
      Lesson(
          id = "1",
          title = "Physics Tutoring",
          description = "Mechanics",
          subject = TutoringSubject.PHYSICS,
          tutorUid = "tutor123",
          studentUid = "student123",
          minPrice = 20.0,
          maxPrice = 40.0,
          timeSlot = "2024-10-10T10:00:00",
          status = LessonStatus.PENDING,
          language = "ENGLISH")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    lessonRepositoryFirestore = LessonRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getNewUid_returnsGeneratedId() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = lessonRepositoryFirestore.getNewUid()
    assert(uid == "1")
  }

  @Test
  fun addLesson_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    lessonRepositoryFirestore.addLesson(lesson, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()
    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateLesson_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    lessonRepositoryFirestore.updateLesson(lesson, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()
    verify(mockDocumentReference).set(any())
  }

  @Test
  fun deleteLesson_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    lessonRepositoryFirestore.deleteLesson(lesson.id, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()
    verify(mockDocumentReference).delete()
  }

  @Test
  fun documentToLesson_returnsValidLesson() {
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString("title")).thenReturn("Physics Tutoring")
    `when`(mockDocumentSnapshot.getString("description")).thenReturn("Mechanics")
    `when`(mockDocumentSnapshot.getString("subject")).thenReturn("PHYSICS")
    `when`(mockDocumentSnapshot.getString("tutorUid")).thenReturn("tutor123")
    `when`(mockDocumentSnapshot.getString("studentUid")).thenReturn("student123")
    `when`(mockDocumentSnapshot.getDouble("minPrice")).thenReturn(20.0)
    `when`(mockDocumentSnapshot.getDouble("maxPrice")).thenReturn(40.0)
    `when`(mockDocumentSnapshot.getString("timeSlot")).thenReturn("2024-10-10T10:00:00")
    `when`(mockDocumentSnapshot.getString("status")).thenReturn("PENDING")
    `when`(mockDocumentSnapshot.getString("language")).thenReturn("ENGLISH")

    val result = lessonRepositoryFirestore.documentToLesson(mockDocumentSnapshot)

    assert(result != null)
    assert(result!!.title == "Physics Tutoring")
    assert(result.subject == TutoringSubject.PHYSICS)
    assert(result.minPrice == 20.0)
    assert(result.maxPrice == 40.0)
    assert(result.status == LessonStatus.PENDING)
    assert(result.language == "ENGLISH")
  }

  @Test
  fun documentToLesson_returnsNullOnInvalidData() {
    `when`(mockDocumentSnapshot.getString("title")).thenReturn(null) // Invalid data

    val result = lessonRepositoryFirestore.documentToLesson(mockDocumentSnapshot)

    assert(result == null)
  }
}
