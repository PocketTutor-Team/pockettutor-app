package com.github.se.project.ui.lesson

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class TutorLessonResponseTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private val mockProfilesRepository = mock(ProfilesRepository::class.java)
  private val mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)
  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  private val mockNavigationActions = mock(NavigationActions::class.java)

  private val mockTutorProfile =
      Profile(
          uid = "100",
          googleUid = "150",
          firstName = "Romeo",
          lastName = "Tutor",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(Language.FRENCH, Language.ENGLISH),
          subjects = listOf(Subject.ALGEBRA, Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 30)

  private val mockStudentProfile =
      Profile(
          uid = "200",
          googleUid = "250",
          firstName = "Juliet",
          lastName = "Student",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.SC,
          academicLevel = AcademicLevel.MA2)

  private val mockRequestedLesson =
      Lesson(
          id = "500",
          title = "Math Tutoring",
          description = "I need help with my math homework please",
          subject = Subject.ALGEBRA,
          languages = listOf(Language.ENGLISH),
          tutorUid = mutableListOf(),
          studentUid = "200",
          minPrice = 10.0,
          maxPrice = 50.0,
          timeSlot = "19/10/2024T10:00:00",
          status = LessonStatus.STUDENT_REQUESTED,
          latitude = 0.0,
          longitude = 0.0)

  private val mockPendingLesson =
      Lesson(
          id = "500",
          title = "Math Tutoring",
          description = "I need help with my math homework please",
          subject = Subject.ALGEBRA,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf("100"),
          studentUid = "200",
          minPrice = 10.0,
          maxPrice = 50.0,
          timeSlot = "19/10/2024T10:00:00",
          status = LessonStatus.PENDING_TUTOR_CONFIRMATION,
          latitude = 0.0,
          longitude = 0.0)

  @Before
  fun setUp() {

    `when`(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }

    `when`(mockProfilesRepository.getProfiles(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(mockTutorProfile, mockStudentProfile)) // Simulate a successful operation
    }

    mockListProfilesViewModel.setCurrentProfile(mockTutorProfile)
  }

  @Test
  fun everyComponentsAreDisplayed_studentRequested() {
    mockLessonViewModel.selectLesson(mockRequestedLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert the screen is displayed
    composeTestRule.onNodeWithTag("tutorLessonResponseScreen").assertIsDisplayed()

    // Assert the top bar is correctly displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmLessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmLessonTitle").assertTextEquals("Confirm the Lesson")

    // Assert the lesson and student details are displayed
    composeTestRule.onNodeWithTag("lessonDetailsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentInfoRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentName").assertTextEquals("Juliet Student")
    composeTestRule.onNodeWithTag("studentAcademicInfo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentAcademicInfo").assertTextEquals("SC - MA2")
    composeTestRule.onNodeWithTag("lessonDetailsColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonSubject").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonSubject").assertTextEquals("ALGEBRA")
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertTextEquals("Math Tutoring")
    composeTestRule.onNodeWithTag("lessonDescription").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("lessonDescription")
        .assertTextEquals("I need help with my math homework please")
    composeTestRule.onNodeWithTag("lessonTime").assertIsDisplayed()

    // Assert the location is displayed
    composeTestRule.onNodeWithTag("lessonLocationColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationTitle").assertTextEquals("Lesson Location")
    composeTestRule.onNodeWithTag("lessonLocationCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationMap").assertIsDisplayed()

    // Assert the confirmation button is displayed
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }

  @Test
  fun everyComponentsAreDisplayed_pendingTutor() {
    mockLessonViewModel.selectLesson(mockPendingLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert the screen is displayed
    composeTestRule.onNodeWithTag("tutorLessonResponseScreen").assertIsDisplayed()

    // Assert the top bar is correctly displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("requestPendingLessonTitle").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("requestPendingLessonTitle")
        .assertTextEquals("Respond to Request")

    // Assert the lesson and student details are displayed
    composeTestRule.onNodeWithTag("lessonDetailsCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentInfoRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentName").assertTextEquals("Juliet Student")
    composeTestRule.onNodeWithTag("studentAcademicInfo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("studentAcademicInfo").assertTextEquals("SC - MA2")
    composeTestRule.onNodeWithTag("lessonDetailsColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonSubject").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonSubject").assertTextEquals("ALGEBRA")
    composeTestRule.onNodeWithTag("lessonTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTitle").assertTextEquals("Math Tutoring")
    composeTestRule.onNodeWithTag("lessonDescription").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("lessonDescription")
        .assertTextEquals("I need help with my math homework please")
    composeTestRule.onNodeWithTag("lessonTime").assertIsDisplayed()

    // Assert the location is displayed
    composeTestRule.onNodeWithTag("lessonLocationColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationTitle").assertTextEquals("Lesson Location")
    composeTestRule.onNodeWithTag("lessonLocationCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonLocationMap").assertIsDisplayed()

    // Assert the confirmation and cancel button are displayed
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
  }

  @Test
  fun confirmLessonWorks_studentRequested() {
    mockLessonViewModel.selectLesson(mockRequestedLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogTitle").assertTextEquals("Confirm Your Offer")
    composeTestRule.onNodeWithTag("confirmDialogText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("confirmDialogText")
        .assertTextEquals(
            "Would you like to offer to teach this lesson at your standard rate of 30.-/hour?")
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()

    // Verify the repository is called to update the lesson
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(screen = Screen.HOME)
  }

  @Test
  fun confirmLessonWorks_pendingTutor() {
    mockLessonViewModel.selectLesson(mockPendingLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogTitle").assertTextEquals("Confirm Your Offer")
    composeTestRule.onNodeWithTag("confirmDialogText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("confirmDialogText")
        .assertTextEquals(
            "Would you like to offer to teach this lesson at your standard rate of 30.-/hour?")
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogConfirmButton").performClick()

    // Verify the repository is called to update the lesson
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(screen = Screen.HOME)
  }

  @Test
  fun confirmThenDismissWorks_studentRequested() {
    mockLessonViewModel.selectLesson(mockRequestedLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogCancelButton").assertIsDisplayed().performClick()

    // Verify the alert dialog has been dismissed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsNotDisplayed()
  }

  @Test
  fun confirmThenDismissWorks_tutorPending() {
    mockLessonViewModel.selectLesson(mockPendingLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogCancelButton").assertIsDisplayed().performClick()

    // Verify the alert dialog has been dismissed
    composeTestRule.onNodeWithTag("confirmDialog").assertIsNotDisplayed()
  }

  @Test
  fun declineLessonWorks_pendingTutor() {
    mockLessonViewModel.selectLesson(mockPendingLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("declineDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("declineDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("declineDialogTitle").assertTextEquals("Dismiss the Lesson")
    composeTestRule.onNodeWithTag("declineDialogText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("declineDialogText")
        .assertTextEquals("Are you sure you want to dismiss this lesson?")
    composeTestRule.onNodeWithTag("declineDialogConfirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("declineDialogConfirmButton").performClick()

    // Verify the repository is called to update the lesson
    verify(mockLessonRepository).updateLesson(any(), any(), any())
    verify(mockNavigationActions).navigateTo(screen = Screen.HOME)
  }

  @Test
  fun declineThenDismissWorks_pendingTutor() {
    mockLessonViewModel.selectLesson(mockPendingLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Confirm the lesson
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed().performClick()

    // Verify the confirm dialog is displayed
    composeTestRule.onNodeWithTag("declineDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("declineDialogCancelButton").assertIsDisplayed().performClick()

    // Verify the alert dialog has been dismissed
    composeTestRule.onNodeWithTag("declineDialog").assertIsNotDisplayed()
  }

  @Test
  fun errorStateDisplayed() {
    val errorLesson = mockRequestedLesson.copy(studentUid = "300")
    mockLessonViewModel.selectLesson(errorLesson)

    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Verify the error message is displayed
    composeTestRule.onNodeWithTag("errorStateColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("errorMessage")
        .assertTextEquals("Cannot retrieve student profile")
  }
}
