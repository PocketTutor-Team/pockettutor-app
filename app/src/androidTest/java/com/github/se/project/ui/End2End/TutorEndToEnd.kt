package com.github.se.project.ui.End2End

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

  @Mock lateinit var navigationActions: NavigationActions

  @Mock lateinit var context: Context

  // Mock du ProfilesRepository
  private val mockProfileRepository = mock(ProfilesRepository::class.java)

  private val mockProfileViewModel = ListProfilesViewModel(mockProfileRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)

  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  private val mockLessons =
    listOf(
      Lesson(
        id = "1",
        title = "Physics Tutoring",
        description = "Mechanics and Thermodynamics",
        subject = Subject.PHYSICS,
        languages = listOf(Language.ENGLISH),
        tutorUid = "mockUid",
        studentUid = "student123",
        minPrice = 20.0,
        maxPrice = 40.0,
        timeSlot = "2024-10-10T10:00:00",
        status = LessonStatus.PENDING,
        latitude = 0.0,
        longitude = 0.0),
      Lesson(
        id = "2",
        title = "Math Tutoring",
        description = "Algebra and Calculus",
        subject = Subject.ANALYSIS,
        languages = listOf(Language.ENGLISH),
        tutorUid = "mockUid",
        studentUid = "student123",
        minPrice = 20.0,
        maxPrice = 40.0,
        timeSlot = "2024-10-10T11:00:00",
        status = LessonStatus.PENDING,
        latitude = 0.0,
        longitude = 0.0)
    )
  private val requestedLessonsFlow = MutableStateFlow(mockLessons)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getNewUid()).thenReturn("mockUid")
  }

  @Test
  fun TutorEndToEndTest() {
    composeTestRule.setContent {
      PocketTutorApp(true, viewModel(), mockProfileViewModel, mockLessonViewModel)
    }
      Thread.sleep(30000)
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
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdownANALYSIS").performClick()
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
    composeTestRule.onNodeWithTag("Find a Student").performClick()
    composeTestRule.onNodeWithTag("Find a Student").performClick()
    composeTestRule.onNodeWithTag("My Work Space").performClick()

    Thread.sleep(5000)
  }
}
