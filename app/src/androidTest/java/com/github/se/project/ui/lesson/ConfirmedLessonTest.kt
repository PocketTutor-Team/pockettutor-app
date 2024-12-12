import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions
import io.getstream.chat.android.client.ChatClient
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
public class ConfirmedLessonTest {

  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private val mockLessonRepository: LessonRepository = mock(LessonRepository::class.java)

  // Mock dependencies
  private val mockProfilesRepository: ProfilesRepository = mock(ProfilesRepository::class.java)
  private val mockNavigationActions: NavigationActions = mock(NavigationActions::class.java)
  private val lessonViewModel = LessonViewModel(mockLessonRepository)
  private val listProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var chatClient: ChatClient

  private val tutorProfile =
      Profile(
          uid = "tutor1",
          token = "",
          googleUid = "67890",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)

  private val studentProfile =
      Profile(
          uid = "student1",
          token = "",
          googleUid = "67890",
          firstName = "James",
          lastName = "Donovan",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.GM,
          academicLevel = AcademicLevel.BA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)

  private val confirmedLesson =
      Lesson(
          id = "lesson1",
          title = "Math Lesson",
          timeSlot = "30/12/2025T14:00:00",
          tutorUid = listOf("tutor1"),
          studentUid = "student1",
          latitude = 37.7749,
          longitude = -122.4194,
          status = LessonStatus.CONFIRMED)

  private val studentRequestedLesson =
      Lesson(
          id = "lesson2",
          title = "Math Lesson",
          timeSlot = "30/12/2025T14:00:00",
          tutorUid = listOf("tutor1"),
          studentUid = "student1",
          latitude = 37.7749,
          longitude = -122.4194,
          status = LessonStatus.STUDENT_REQUESTED)

  private val pendingTutorConfirmationLesson =
      Lesson(
          id = "lesson3",
          title = "Math Lesson",
          timeSlot = "30/12/2025T14:00:00",
          tutorUid = listOf("tutor1"),
          studentUid = "student1",
          latitude = 37.7749,
          longitude = -122.4194,
          status = LessonStatus.PENDING_TUTOR_CONFIRMATION)

  @Before
  fun setUp() {
    whenever(mockProfilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(
          listOf(
              tutorProfile,
              studentProfile)) // Simulate a list of profiles with our beloved Ozymandias
    }

    whenever(mockLessonRepository.getLessonsForTutor(eq(tutorProfile.uid), any(), any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
          onSuccess(listOf(confirmedLesson, studentRequestedLesson, pendingTutorConfirmationLesson))
        }

    whenever(mockLessonRepository.deleteLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful deletion
    }

    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }

    listProfilesViewModel.getProfiles()
    lessonViewModel.getLessonsForTutor(tutorProfile.uid, {})

    lessonViewModel.selectLesson(confirmedLesson)
    listProfilesViewModel.setCurrentProfile(tutorProfile)

    // Mock ChatViewModel
    chatViewModel = mock(ChatViewModel::class.java)
    doNothing().`when`(chatViewModel).connectUser(any())
  }

  /*@Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_ConfirmedLesson() {
    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_StudentRequestedLesson() {
    lessonViewModel.selectLesson(studentRequestedLesson)

    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("cancelRequestButton").assertIsDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_PendingLesson() {
    lessonViewModel.selectLesson(pendingTutorConfirmationLesson)
    listProfilesViewModel.setCurrentProfile(studentProfile)

    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenBackButtonClicked() {
    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(mockNavigationActions).goBack()
  }*/
  /*
   @Test
   fun confirmedLessonScreenOpensSmsApp_ConfirmedLesson() {
     var isLocationChecked = false

     composeTestRule.setContent {
       ConfirmedLessonScreen(
           listProfilesViewModel = listProfilesViewModel,
           lessonViewModel = lessonViewModel,
           navigationActions = mockNavigationActions,
           chatViewModel = chatViewModel,
           onLocationChecked = { isLocationChecked = true })
     }
     composeTestRule.waitForIdle()

     composeTestRule.waitUntil(15000) {
       // wait max 15 seconds for the map to load,
       // as soon as the map is ready, the next line will be executed
       isLocationChecked
     }
     isLocationChecked = false

     // Click on the "Message Tutor/Student" button
     composeTestRule.onNodeWithTag("contactButton").performClick()
   }

   @Test
   fun confirmedLessonScreenNoProfileFound() {
     // Mock no profile found
     listProfilesViewModel.setCurrentProfile(null)

     composeTestRule.setContent {
       ConfirmedLessonScreen(
           listProfilesViewModel = listProfilesViewModel,
           lessonViewModel = lessonViewModel,
           chatViewModel = chatViewModel,
           navigationActions = mockNavigationActions)
     }

     composeTestRule.onNodeWithText("No profile found. Should not happen.").assertIsDisplayed()
   }

  */

  /*@Test
  fun confirmedLessonScreenNoLessonSelected() {
    // Mock no lesson selected
    lessonViewModel.unselectLesson()

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("No lesson selected. Should not happen.").assertIsDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenCancellationButtonClicked() {
    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed().performClick()
    Thread.sleep(300) // Wait for the dialog to show up

    // Check that the cancellation dialog is well displayed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogDismissButton").assertIsDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenCancellationDialogDismissed() {
    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed().performClick()
    Thread.sleep(300) // Wait for the dialog to show up

    // Dismiss the dialog
    composeTestRule.onNodeWithTag("cancelDialogDismissButton").assertIsDisplayed().performClick()

    // Check that the dialog is dismissed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsNotDisplayed()
  }*/

  /*@Test
  fun confirmedLessonScreenCancellationDialogConfirmed_ConfirmedLesson() {
    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed().performClick()
    Thread.sleep(300) // Wait for the dialog to show up

    // Confirm the dialog
    composeTestRule.onNodeWithTag("cancelDialogConfirmButton").assertIsDisplayed().performClick()

    // Check that the cancellation has been confirmed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsNotDisplayed()
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }*/

  /*@Test
  fun confirmedLessonScreenLessonCancellation_StudentRequestedLesson() {
    lessonViewModel.selectLesson(studentRequestedLesson)

    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("cancelRequestButton").assertIsDisplayed().performClick()

    // Check that the cancellation has been confirmed
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }*/

  /*@Test
  fun confirmedLessonScreenLessonCancellation_PendingLesson() {
    lessonViewModel.selectLesson(pendingTutorConfirmationLesson)
    listProfilesViewModel.setCurrentProfile(studentProfile)

    var isLocationChecked = false

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          onLocationChecked = { isLocationChecked = true })
    }
    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      isLocationChecked
    }
    isLocationChecked = false

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed().performClick()

    // Check that the cancellation has been confirmed
    verify(mockLessonRepository).deleteLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }*/
}
