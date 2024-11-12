package com.github.se.project.ui.lesson

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class TutorLessonResponseTest {
  @get:Rule val composeTestRule = createComposeRule()

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

  private val mockPendingLesson =
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

    mockLessonViewModel.selectLesson(mockPendingLesson)
    mockListProfilesViewModel.setCurrentProfile(mockTutorProfile)
  }

  @Test
  fun everyComponentsAreDisplayed() {
    composeTestRule.setContent {
      TutorLessonResponseScreen(
          mockListProfilesViewModel, mockLessonViewModel, mockNavigationActions)
    }
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val allowButton = device.findObject(UiSelector().text("While using the app"))
    if (allowButton.exists() && allowButton.isEnabled) {
      allowButton.click()
    }

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
    composeTestRule.onNodeWithTag("lessonDate").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonDate").assertTextEquals("Saturday, 19 October 2024")
    composeTestRule.onNodeWithTag("lessonTime").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lessonTime").assertTextEquals("10:00")

    // Assert the confirmation button is displayed
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }
}
