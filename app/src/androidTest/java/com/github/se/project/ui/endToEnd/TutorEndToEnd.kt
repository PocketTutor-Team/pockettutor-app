package com.github.se.project.ui.endToEnd

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
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
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(mockStudent)) // Simulate a list of profiles with our beloved Ozymandias
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
  }

  @Test
  fun TutorEndToEndTest() {
    composeTestRule.setContent {
      PocketTutorApp(true, viewModel(), mockProfileViewModel, mockLessonViewModel)
    }
    // Thread.sleep(50000)
    // Sign In Screen
    composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").performClick()
    composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()

    // Create Profile Screen
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0213456789")
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
    composeTestRule.onNodeWithContentDescription("Profile Icon").performClick()
    composeTestRule.onNodeWithTag("profileStatus").assertTextEquals("Status: BA3 Tutor")
    composeTestRule.onNodeWithTag("profileSection").assertTextEquals("Section: SC")
    composeTestRule.onNodeWithTag("profilePrice").assertTextEquals("Price: 50.- per hour")
    composeTestRule
        .onNodeWithTag("lessonsCount")
        .assertTextEquals("0 lessons given since you joined PocketTutor")
    composeTestRule.onNodeWithTag("closeButton").performClick()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()
    composeTestRule.onNodeWithTag("Find a Student").performClick()
    composeTestRule.onNodeWithTag("screenTitle").assertExists()
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("physics").assertIsDisplayed()
    composeTestRule.onNodeWithText("physics").performClick()
    Thread.sleep(5000)
    composeTestRule.onNodeWithTag("tutorLessonResponseScreen").assertExists()
    composeTestRule.onNodeWithText("Ozymandias Halifax").assertExists()
    composeTestRule.onNodeWithText("Offer to Teach (50.-/hour)").assertExists()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule
        .onNodeWithText(
            "Would you like to offer to teach this lesson at your standard rate of 50.-/hour?")
        .assertExists()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertExists()
    composeTestRule
        .onNodeWithTag("section_Waiting for the Student Confirmation")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()

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
    val updatedMockLessonFlow = MutableStateFlow(mockLessons[0])
    doReturn(updatedMockLessonFlow).`when`(mockLessonViewModel).selectedLesson
    // Reload the screen
    composeTestRule.onNodeWithContentDescription("Profile Icon").performClick()
    composeTestRule.onNodeWithTag("closeButton").performClick()
    //
    composeTestRule.onNodeWithTag("section_Waiting for your Confirmation").assertExists()
    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()
    composeTestRule.onNodeWithText("Maths Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("analysis").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonCard_0").performClick()
    composeTestRule.onNodeWithText("Ozymandias Halifax").assertExists()

    composeTestRule.onNodeWithTag("cancelButton").performClick()
    composeTestRule.onNodeWithText("Are you sure you want to dismiss this lesson?").assertExists()
    composeTestRule.onNodeWithText("Cancel").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule
        .onNodeWithText(
            "Would you like to offer to teach this lesson at your standard rate of 50.-/hour?")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()

    composeTestRule.onNodeWithTag("section_Upcoming Lessons").assertExists()
    composeTestRule.onNodeWithTag("lessonCard_0").assertExists()
    composeTestRule.onNodeWithText("Student: Ozymandias Halifax").assertIsDisplayed()
  }
}
