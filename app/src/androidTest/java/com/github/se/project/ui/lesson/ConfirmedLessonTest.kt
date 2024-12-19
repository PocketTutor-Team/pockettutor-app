package com.github.se.project.ui.lesson

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.R
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
import com.github.se.project.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ConfirmedLessonTest {

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
  private val mockChatViewModel = mock(ChatViewModel::class.java)

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

  private var isLocationChecked = false

  private val context = ApplicationProvider.getApplicationContext<Context>()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions,
          chatViewModel = mockChatViewModel,
          onLocationChecked = { isLocationChecked = true })
    }
    whenever(mockProfilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(
          listOf(
              tutorProfile,
              studentProfile)) // Simulate a list of profiles with our beloved Ozymandias
    }

    whenever(mockLessonRepository.getLessonsForTutor(eq(tutorProfile.uid), any(), any()))
        .thenAnswer { invocation ->
          @Suppress("UNCHECKED_CAST")
          val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
          onSuccess(listOf(confirmedLesson, studentRequestedLesson, pendingTutorConfirmationLesson))
        }

    whenever(mockLessonRepository.deleteLesson(any(), any(), any())).thenAnswer { invocation ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful deletion
    }

    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }

    listProfilesViewModel.getProfiles()
    lessonViewModel.getLessonsForTutor(tutorProfile.uid) {}

    lessonViewModel.selectLesson(confirmedLesson)
    listProfilesViewModel.setCurrentProfile(tutorProfile)
  }

  @Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_ConfirmedLesson() {
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").performScrollTo().assertIsDisplayed()

    composeTestRule.onNodeWithTag("cancelButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_StudentRequestedLesson() {
    lessonViewModel.selectLesson(studentRequestedLesson)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("cancelRequestButton").performScrollTo().performScrollTo()
    composeTestRule.onNodeWithTag("cancelRequestButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly_PendingLesson() {
    lessonViewModel.selectLesson(pendingTutorConfirmationLesson)
    listProfilesViewModel.setCurrentProfile(studentProfile)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("deleteButton").performScrollTo()

    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenBackButtonClicked() {

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun confirmedLessonScreenNoProfileFound() {
    // Mock no profile found
    listProfilesViewModel.setCurrentProfile(null)

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(20000) {
      composeTestRule.onNodeWithText(context.getString(R.string.no_profile_selected)).isDisplayed()
    }

    composeTestRule
        .onNodeWithText(context.getString(R.string.no_profile_selected))
        .assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenNoLessonSelected() {
    // Mock no lesson selected
    lessonViewModel.unselectLesson()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(20000) {
      composeTestRule.onNodeWithText(context.getString(R.string.no_lesson_selected)).isDisplayed()
    }

    composeTestRule
        .onNodeWithText(context.getString(R.string.no_lesson_selected))
        .assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenCancellationButtonClicked() {

    composeTestRule.onNodeWithTag("cancelButton").performScrollTo()

    composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithTag("cancelButton").isDisplayed() }

    composeTestRule.waitForIdle()

    // Click on the "Cancel Lesson" button
    composeTestRule
        .onNodeWithTag("cancelButton")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("cancelDialog").isDisplayed() }

    // Check that the cancellation dialog is well displayed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelDialogDismissButton").assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenCancellationDialogDismissed() {

    composeTestRule.waitForIdle()

    // Click on the "Cancel Lesson" button
    composeTestRule
        .onNodeWithTag("cancelButton")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("cancelDialog").isDisplayed() }

    // Dismiss the dialog
    composeTestRule.onNodeWithTag("cancelDialogDismissButton").assertIsDisplayed().performClick()

    // Check that the dialog is dismissed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsNotDisplayed()
  }

  @Test
  fun confirmedLessonScreenCancellationDialogConfirmed_ConfirmedLesson() {

    composeTestRule.waitForIdle()

    // Click on the "Cancel Lesson" button
    composeTestRule
        .onNodeWithTag("cancelButton")
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("cancelDialog").isDisplayed() }

    // Confirm the dialog
    composeTestRule.onNodeWithTag("cancelDialogConfirmButton").assertIsDisplayed().performClick()

    composeTestRule.waitUntil(20000) {
      composeTestRule.onNodeWithTag("cancelDialog").isNotDisplayed()
    }

    // Check that the cancellation has been confirmed
    composeTestRule.onNodeWithTag("cancelDialog").assertIsNotDisplayed()
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }

  @Test
  fun confirmedLessonScreenLessonCancellation_StudentRequestedLesson() {
    lessonViewModel.selectLesson(studentRequestedLesson)

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("cancelRequestButton").performScrollTo()

    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("cancelRequestButton").assertIsDisplayed().performClick()

    // Check that the cancellation has been confirmed
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }

  @Test
  fun confirmedLessonScreenLessonCancellation_PendingLesson() {
    lessonViewModel.selectLesson(pendingTutorConfirmationLesson)
    listProfilesViewModel.setCurrentProfile(studentProfile)

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("deleteButton").performScrollTo()
    // Click on the "Cancel Lesson" button
    composeTestRule.onNodeWithTag("deleteButton").assertIsDisplayed().performClick()

    // Check that the cancellation has been confirmed
    verify(mockLessonRepository).deleteLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }
}
