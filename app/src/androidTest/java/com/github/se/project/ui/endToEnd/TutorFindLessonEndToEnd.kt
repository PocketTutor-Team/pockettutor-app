package com.github.se.project.ui.endToEnd

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessaging
import io.getstream.chat.android.models.Filters.eq
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class TutorEndToEndTest {

  @Mock lateinit var navigationActions: NavigationActions

  @Mock lateinit var context: Context

  // Mock du ProfilesRepository
  private val mockProfileRepository = mock(ProfilesRepository::class.java)

  private val mockProfileViewModel = ListProfilesViewModel(mockProfileRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)

  private var mockLessonViewModel = spy(LessonViewModel(mockLessonRepository))

  private val mockCertificationViewModel = mock(CertificationViewModel::class.java)

  private val mockIsConnected = MutableStateFlow(true)
  private lateinit var networkStatusViewModel: NetworkStatusViewModel

  private var mockChatViewModel = mock(ChatViewModel::class.java)

  private var mockLessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf(),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 50.0,
              timeSlot = "16/11/2024T12:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 46.518973490411526,
              longitude = 6.5685102716088295),
      )

  private val mockStudent =
      Profile(
          uid = "student123",
          token = "",
          googleUid = "mockTutor",
          firstName = "Ozymandias",
          lastName = "Halifax",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA3,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.AICC),
          schedule = List(7) { List(12) { 0 } })

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val grantNotificationPermission: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    val mockFirebaseMessaging = mock<FirebaseMessaging>()
    whenever(mockFirebaseMessaging.token).thenReturn(Tasks.forResult("mock_fcm_token"))
    whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(mockStudent)) // Simulate a list of profiles with our beloved Ozymandias
    }
    whenever(mockProfileRepository.updateToken(any(), any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.arguments[2] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getNewUid()).thenReturn("mockUid")
    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      mockLessons = listOf(invocation.arguments[0] as Lesson)
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess()
    }
    whenever(mockLessonRepository.getLessonsForTutor(eq("mockUid"), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
      onSuccess(mockLessons)
    }
    whenever(mockLessonRepository.getAllRequestedLessons(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Lesson>) -> Unit
      onSuccess(mockLessons)
    }
    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      mockLessons = listOf(invocation.arguments[0] as Lesson)
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    val mockLessonFlow = MutableStateFlow<Lesson?>(mockLessons[0])
    doReturn(mockLessonFlow).`when`(mockLessonViewModel).selectedLesson
    mockLessonFlow.value = mockLessons[0]

    networkStatusViewModel =
        object :
            NetworkStatusViewModel(
                application = androidx.test.core.app.ApplicationProvider.getApplicationContext()) {
          override val isConnected = mockIsConnected
        }
  }

  @Test
  fun tutorProfileCreationAndFirstLessonTest() {
    var testMapReady = false
    composeTestRule.setContent {
      PocketTutorApp(
          true,
          viewModel(),
          mockProfileViewModel,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = { testMapReady = it },
          chatViewModel = mockChatViewModel)
    }
    composeTestRule.waitForIdle()

    // Sign In Screen
    // composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.onNodeWithTag("firstNameField").performScrollTo().assertIsDisplayed()

    // Create Profile Screen
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0213456789")
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo()
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    assertEquals(Role.TUTOR, mockProfileViewModel.currentProfile.value?.role)

    // Create Tutor Profile Screen
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("checkbox_FRENCH").performClick()
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdownANALYSIS").performClick()
    composeTestRule.onNodeWithTag("dropdownPHYSICS").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("priceSlider").performGesture { swipeRight() }
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Create Tutor Schedule Screen
    composeTestRule
        .onNodeWithTag("welcomeText")
        .assertTextEquals("John, show us your availabilities")
    composeTestRule.onNodeWithTag("Slot_0_0").performClick()
    composeTestRule.onNodeWithTag("Slot_0_3").performClick()
    composeTestRule.onNodeWithTag("Slot_0_2").performClick()
    composeTestRule.onNodeWithTag("Slot_0_6").performClick()
    composeTestRule.onNodeWithTag("FindStudentButton").performClick()

    // Home Screen
    composeTestRule.onNodeWithTag("profile_photo").performClick()
    composeTestRule.onNodeWithTag("profileAcademicInfo").assertTextEquals("SC - BA3")
    composeTestRule.onNodeWithTag("profileInfoRole").assertTextEquals("Tutor")
    composeTestRule.onNodeWithTag("lessonsCount").assertTextEquals("0")
    composeTestRule.onNodeWithTag("closeButton").performClick()
    composeTestRule.onNodeWithTag("profile_photo").assertExists()
    composeTestRule.onNodeWithTag("middlePlus").performClick()
    composeTestRule.onNodeWithTag("screenTitle").assertExists()
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Physics").assertIsDisplayed()
    composeTestRule.onNodeWithText("Physics").performClick()
    // work
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("tutorLessonResponseScreen").assertExists()
    composeTestRule.onNodeWithText("Ozymandias Halifax").assertExists()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()
    composeTestRule.onNodeWithTag("profile_photo").assertExists()
    composeTestRule
        .onNodeWithTag("section_Waiting for the Student Confirmation")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()
  }

  @Test
  fun TutorMatchAndReviewConsultation() {
    var testMapReady = false
    composeTestRule.setContent {
      PocketTutorApp(
          true,
          viewModel(),
          mockProfileViewModel,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = { testMapReady = it },
          chatViewModel = mockChatViewModel)
    }
    composeTestRule.waitForIdle()

    // Sign In Screen
    // composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.onNodeWithTag("firstNameField").performScrollTo().assertIsDisplayed()

    // Create Profile Screen
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0213456789")
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo()
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    assertEquals(Role.TUTOR, mockProfileViewModel.currentProfile.value?.role)

    // Create Tutor Profile Screen
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("checkbox_FRENCH").performClick()
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdownANALYSIS").performClick()
    composeTestRule.onNodeWithTag("dropdownPHYSICS").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("priceSlider").performGesture { swipeRight() }
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Create Tutor Schedule Screen
    composeTestRule
        .onNodeWithTag("welcomeText")
        .assertTextEquals("John, show us your availabilities")
    composeTestRule.onNodeWithTag("Slot_0_0").performClick()
    composeTestRule.onNodeWithTag("Slot_0_3").performClick()
    composeTestRule.onNodeWithTag("Slot_0_2").performClick()
    composeTestRule.onNodeWithTag("Slot_0_6").performClick()
    composeTestRule.onNodeWithTag("FindStudentButton").performClick()
    mockLessons =
        listOf(
            Lesson(
                id = "1",
                title = "Maths Tutoring",
                description = "Fourrier Transform",
                subject = Subject.ANALYSIS,
                languages = listOf(Language.ENGLISH),
                tutorUid = listOf("mockUid"),
                studentUid = "student123",
                minPrice = 20.0,
                maxPrice = 50.0,
                timeSlot = "16/11/2024T12:00:00",
                status = LessonStatus.PENDING_TUTOR_CONFIRMATION,
                latitude = 46.518973490411526,
                longitude = 6.5685102716088295),
        )
    // Call the updatelesson
    var updatedMockLessonFlow = MutableStateFlow(mockLessons[0])
    // val updatedMockLessonFlow2 = MutableStateFlow(mockLessons[1])
    doReturn(updatedMockLessonFlow).`when`(mockLessonViewModel).selectedLesson
    // Reload the screen

    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()
    composeTestRule.onNodeWithText("Maths Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Analysis").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonCard_0").performClick()
    composeTestRule.onNodeWithText("Ozymandias Halifax").assertExists()

    composeTestRule.onNodeWithTag("cancelButton").performScrollTo().performClick()
    composeTestRule.onNodeWithText("Cancel").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()

    composeTestRule.onNodeWithTag("section_Upcoming Lessons").assertExists()
    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()
    composeTestRule.onNodeWithText("Student: Ozymandias Halifax").assertIsDisplayed()
    composeTestRule.onNodeWithText("Maths Tutoring").performClick()
    composeTestRule.onNodeWithTag("backButton").performClick()

    mockLessons =
        listOf(
            Lesson(
                id = "1",
                title = "Maths Tutoring",
                description = "Fourrier Transform",
                subject = Subject.ANALYSIS,
                languages = listOf(Language.ENGLISH),
                tutorUid = listOf("mockUid"),
                studentUid = "student123",
                minPrice = 20.0,
                maxPrice = 50.0,
                rating = LessonRating(5, "Really Good, Liked it!", Timestamp.now(), true),
                timeSlot = "16/11/2024T12:00:00",
                status = LessonStatus.COMPLETED,
                latitude = 46.518973490411526,
                longitude = 6.5685102716088295),
        )
    // doReturn(updatedMockLessonFlow2).`when`(mockLessonViewModel).selectedLesson
    composeTestRule.onNodeWithTag("profile_photo").performClick()
    composeTestRule.onNodeWithTag("lessonsCount").assertTextEquals("1")
    composeTestRule.onNodeWithText("Maths Tutoring").performClick()
    composeTestRule.onNodeWithText("Lesson not rated yet!").assertExists()
  }
}
