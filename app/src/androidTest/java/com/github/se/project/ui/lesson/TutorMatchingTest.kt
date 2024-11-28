package com.github.se.project.ui.lesson

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TutorMatchingScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController

  private val profileFlow =
      MutableStateFlow(
          Profile(
              uid = "uid",
              token = "",
              googleUid = "googleUid",
              firstName = "First",
              lastName = "Last",
              phoneNumber = "1234567890",
              role = Role.STUDENT,
              section = Section.GM,
              academicLevel = AcademicLevel.MA2,
              languages = listOf(),
              subjects = listOf(),
              schedule = List(7) { List(12) { 1 } }))

  private val lessonFlow =
      MutableStateFlow(
          Lesson(
              id = "lessonId",
              title = "Math Lesson",
              description = "Algebra",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 10.0,
              maxPrice = 50.0,
              timeSlot = "12/12/2024T12:00:00",
              status = LessonStatus.MATCHING,
              latitude = 0.0,
              longitude = 0.0))

  private val tutorsFlow =
      MutableStateFlow(
          listOf(
              Profile(
                  uid = "tutor123",
                  token = "",
                  googleUid = "googleUid",
                  firstName = "Tutor",
                  lastName = "One",
                  phoneNumber = "0987654321",
                  role = Role.TUTOR,
                  section = Section.IN,
                  academicLevel = AcademicLevel.MA1,
                  languages = listOf(Language.ENGLISH),
                  subjects = listOf(Subject.ANALYSIS),
                  schedule = List(7) { List(12) { 1 } },
                  price = 30)))

  @Before
  fun setup() {
    // Mock dependencies
    profilesRepository = mock(ProfilesRepository::class.java)
    lessonRepository = mock(LessonRepository::class.java)
    navController = mock()

    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
    listProfilesViewModel = spy(listProfilesViewModel)

    lessonViewModel = LessonViewModel(lessonRepository)
    lessonViewModel = spy(lessonViewModel)

    navigationActions = NavigationActions(navController)
    navigationActions = spy(navigationActions)

    // Stub navigation actions
    doNothing().`when`(navigationActions).navigateTo(anyString())
    doNothing().`when`(navigationActions).goBack()

    // Mock flow properties on ViewModels
    whenever(listProfilesViewModel.currentProfile).thenReturn(profileFlow)
    whenever(lessonViewModel.selectedLesson).thenReturn(lessonFlow)

    // Correctly mock the profiles flow

    // Stub repository methods to simulate successful data retrieval
    whenever(profilesRepository.init(any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as () -> Unit
      onSuccess() // Simulate successful initialization
    }

    whenever(profilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(tutorsFlow.value) // Simulate returning an empty list of profiles
    }

    whenever(lessonRepository.addLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }

    listProfilesViewModel.getProfiles()
  }

  @Test
  fun testNoTutorsMessageDisplayed_whenNoMatchingTutors() {
    // Set tutor list to empty
    tutorsFlow.value = emptyList()
    listProfilesViewModel.getProfiles()

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the no tutor message is displayed
    composeTestRule.onNodeWithTag("noTutorMessage").assertIsDisplayed()
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the top bar is correctly displayed
    composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("AvailableTutorsTitle")
        .assertIsDisplayed()
        .assertTextEquals("Available Tutors")
    composeTestRule.onNodeWithTag("filterButton").assertIsDisplayed()

    // Verify the tutor list is displayed
    composeTestRule.onNodeWithTag("tutorsList").assertIsDisplayed()

    // Verify the bottom bar is displayed
    composeTestRule.onNodeWithTag("noTutorButton").assertIsDisplayed()
  }

  @Test
  fun testConfirmButton_whenNoTutorSelected() {
    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("noTutorButton").performClick()
    verify(navigationActions).navigateTo(Screen.HOME)
  }

  @Test
  fun selectAndConfirmLessonWithTutor() {
    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Select a tutor
    composeTestRule.onNodeWithTag("tutorsList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorCard_0").assertIsDisplayed().performClick()

    // Verify the user is navigated to the selected tutor screen
    verify(navigationActions).navigateTo(Screen.SELECTED_TUTOR_DETAILS)
  }

  @Test
  fun goBackButton_navigateBack() {
    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()
    verify(navigationActions).goBack()
  }

  @Test
  fun correctButtonAreDisplayed_whenStatusIsStudentRequested() {
    lessonFlow.value = lessonFlow.value.copy(status = LessonStatus.STUDENT_REQUESTED)

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("noTutorButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("cancellationButton").assertIsDisplayed()
  }

  @Test
  fun lessonCancellationDialogIsDisplayed_whenCancellationButtonClicked() {
    lessonFlow.value = lessonFlow.value.copy(status = LessonStatus.STUDENT_REQUESTED)

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("cancellationButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("cancellationDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancellationDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancellationDialogText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancellationDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancellationDialogDismissButton").assertIsDisplayed()
  }

  @Test
  fun lessonDeleted_whenCancellationDialogConfirmed() {
    lessonFlow.value = lessonFlow.value.copy(status = LessonStatus.STUDENT_REQUESTED)

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Click on the cancellation button and confirm the dialog
    composeTestRule.onNodeWithTag("cancellationButton").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithTag("cancellationDialogConfirmButton")
        .assertIsDisplayed()
        .performClick()

    // Verify the dialog is dismissed and the navigation is done
    composeTestRule.onNodeWithTag("cancellationDialog").assertIsNotDisplayed()
    verify(navigationActions).goBack()
  }

  @Test
  fun dialogDismissed_whenCancellationDialogDismissed() {
    lessonFlow.value = lessonFlow.value.copy(status = LessonStatus.STUDENT_REQUESTED)

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Click on the cancellation button and dismiss the dialog
    composeTestRule.onNodeWithTag("cancellationButton").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithTag("cancellationDialogDismissButton")
        .assertIsDisplayed()
        .performClick()

    // Verify the dialog was dismissed
    composeTestRule.onNodeWithTag("cancellationDialog").assertIsNotDisplayed()
  }
}
