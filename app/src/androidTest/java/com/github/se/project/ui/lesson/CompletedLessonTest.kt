package com.github.se.project.ui.lesson

import CompletedLessonScreen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.*
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class CompletedLessonScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockProfilesRepository = mock(ProfilesRepository::class.java)
  private val mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)
  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  private val mockNavigationActions = mock(NavigationActions::class.java)

  private val mockTutorProfile =
      Profile(
          uid = "tutor1",
          googleUid = "googleTutor1",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)

  private val mockStudentProfile =
      Profile(
          uid = "student1",
          googleUid = "googleStudent1",
          firstName = "Jane",
          lastName = "Smith",
          phoneNumber = "0987654321",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA1,
          schedule = List(7) { List(12) { 0 } },
          price = 0)

  private val lessonWithRating =
      Lesson(
          id = "lesson1",
          title = "Math Lesson",
          timeSlot = "30/12/2024T14:00:00",
          tutorUid = listOf(mockTutorProfile.uid),
          studentUid = mockStudentProfile.uid,
          subject = Subject.AICC,
          latitude = 0.0,
          longitude = 0.0,
          status = LessonStatus.COMPLETED,
          rating = LessonRating(grade = 4, comment = "Great lesson!"))

  private val lessonWithoutRating = lessonWithRating.copy(rating = null)

  private val lessonWithoutComment =
      lessonWithRating.copy(rating = lessonWithRating.rating?.copy(comment = ""))

  @Before
  fun setUp() {
    // Mock getProfiles
    whenever(mockProfilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(mockTutorProfile, mockStudentProfile))
    }

    mockListProfilesViewModel.getProfiles()
  }

  @Test
  fun everyComponentsAreDisplayedWithRatingAndComment() {
    // Set current user as student
    mockListProfilesViewModel.setCurrentProfile(mockStudentProfile)
    mockLessonViewModel.selectLesson(lessonWithRating)

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert the screen is displayed
    composeTestRule.onNodeWithTag("completedLessonScreen").assertIsDisplayed()

    // Assert the top bar is correctly displayed
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()

    // Assert role information
    composeTestRule
        .onNodeWithTag("roleInformation")
        .assertIsDisplayed()
        .assertTextEquals("Tutor and lesson information")

    // Assert lesson details are displayed
    composeTestRule.onNodeWithTag("lessonDetails").assertIsDisplayed()

    // Assert review header
    composeTestRule
        .onNodeWithTag("reviewHeader")
        .assertIsDisplayed()
        .assertTextEquals("Your Review")

    // Assert review card
    composeTestRule.onNodeWithTag("reviewCard").assertIsDisplayed()

    // Assert student's name in the review
    composeTestRule.onNodeWithTag("studentName").assertIsDisplayed().assertTextEquals("Jane Smith")

    // Assert lesson info
    composeTestRule.onNodeWithTag("lessonInfo").assertIsDisplayed()

    // Assert rating stars
    composeTestRule.onNodeWithTag("ratingStars").assertIsDisplayed()

    // Assert review comment
    composeTestRule
        .onNodeWithTag("reviewComment")
        .assertIsDisplayed()
        .assertTextEquals("Great lesson!")
  }

  @Test
  fun displaysNotRatedMessageWhenNoRating() {
    // Set current user as student
    mockListProfilesViewModel.setCurrentProfile(mockStudentProfile)
    mockLessonViewModel.selectLesson(lessonWithoutRating)

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert not rated card is displayed
    composeTestRule.onNodeWithTag("notRatedCard").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("notRatedText")
        .assertIsDisplayed()
        .assertTextEquals("Lesson not rated yet!")
  }

  @Test
  fun displaysNoCommentMessageWhenNoComment() {
    // Set current user as student
    mockListProfilesViewModel.setCurrentProfile(mockStudentProfile)
    mockLessonViewModel.selectLesson(lessonWithoutComment)

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert review card is displayed
    composeTestRule.onNodeWithTag("reviewCard").assertIsDisplayed()

    // Assert "Lesson not commented yet!" message is displayed
    composeTestRule
        .onNodeWithTag("noCommentText")
        .assertIsDisplayed()
        .assertTextEquals("Lesson not commented yet!")
  }

  @Test
  fun backButtonNavigatesBack() {
    // Set current user as student
    mockListProfilesViewModel.setCurrentProfile(mockStudentProfile)
    mockLessonViewModel.selectLesson(lessonWithRating)

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Click on back button
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()

    // Verify navigation back is called
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun displaysErrorWhenNoLessonSelected() {
    // Set current user as student
    mockListProfilesViewModel.setCurrentProfile(mockStudentProfile)
    mockLessonViewModel.unselectLesson()

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert error message is displayed
    composeTestRule
        .onNodeWithTag("errorText")
        .assertIsDisplayed()
        .assertTextEquals("Error. Should not happen")
  }

  @Test
  fun displaysErrorWhenNoProfileFound() {
    // No current user profile
    mockListProfilesViewModel.setCurrentProfile(null)
    mockLessonViewModel.selectLesson(lessonWithRating)

    composeTestRule.setContent {
      CompletedLessonScreen(
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    // Assert error message is displayed
    composeTestRule
        .onNodeWithTag("errorText")
        .assertIsDisplayed()
        .assertTextEquals("Error. Should not happen")
  }
}
