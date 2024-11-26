package com.github.se.project.ui.lesson

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
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
import com.google.firebase.Timestamp
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SelectedTutorDetailsTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel
  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController

  private val rating =
      LessonRating(
          grade = 5,
          comment = "Really great tutor!",
          date =
              Timestamp(
                  Calendar.getInstance().apply { set(2024, Calendar.OCTOBER, 19, 10, 0, 0) }.time))

  private val studentProfileFlow =
      MutableStateFlow(
          Profile(
              uid = "student123",
              googleUid = "googleUid",
              firstName = "John",
              lastName = "Student",
              phoneNumber = "1234567890",
              role = Role.STUDENT,
              section = Section.GM,
              academicLevel = AcademicLevel.MA2))

  private val tutorProfileFlow =
      MutableStateFlow(
          Profile(
              uid = "tutor123",
              googleUid = "googleUid",
              firstName = "Elena",
              lastName = "Tutor",
              phoneNumber = "1234567890",
              role = Role.TUTOR,
              section = Section.IN,
              academicLevel = AcademicLevel.PhD,
              description = "I am the best tutor haha",
              languages = listOf(Language.FRENCH, Language.ENGLISH),
              subjects = listOf(Subject.ANALYSIS, Subject.ALGEBRA),
              schedule = List(7) { List(12) { 0 } },
              price = 30))

  private val completedLesson =
      Lesson(
          id = "completedLesson",
          title = "Past Math Lesson",
          description = "Past Algebra lesson",
          subject = Subject.ANALYSIS,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf("tutor123"),
          studentUid = "student123",
          minPrice = 10.0,
          maxPrice = 50.0,
          timeSlot = "19/10/2024T10:00:00",
          status = LessonStatus.COMPLETED,
          latitude = 0.0,
          longitude = 0.0,
          rating = rating)

  private val currentLessonFlow =
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
              timeSlot = "19/10/2024T10:00:00",
              status = LessonStatus.MATCHING,
              latitude = 0.0,
              longitude = 0.0))

  private val completedLessonsFlow = MutableStateFlow(listOf(completedLesson))

  @Before
  fun setup() {
    // Mock dependencies
    profilesRepository = mock(ProfilesRepository::class.java)
    lessonRepository = mock(LessonRepository::class.java)
    navController = mock()

    listProfilesViewModel = spy(ListProfilesViewModel(profilesRepository))
    lessonViewModel = spy(LessonViewModel(lessonRepository))
    navigationActions = spy(NavigationActions(navController))

    // Stub navigation actions
    doNothing().`when`(navigationActions).navigateTo(anyString())

    // Mock flow properties
    whenever(listProfilesViewModel.currentProfile).thenReturn(studentProfileFlow)
    whenever(listProfilesViewModel.selectedProfile).thenReturn(tutorProfileFlow)
    whenever(lessonViewModel.selectedLesson).thenReturn(currentLessonFlow)
    whenever(lessonViewModel.currentUserLessons).thenReturn(completedLessonsFlow)

    // Stub repository methods
    whenever(profilesRepository.init(any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as () -> Unit
      onSuccess()
    }

    whenever(lessonRepository.addLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess()
    }

    whenever(profilesRepository.getProfiles(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(studentProfileFlow.value, tutorProfileFlow.value))
    }

    listProfilesViewModel.getProfiles()
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Check top bar
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmLessonTitle").assertIsDisplayed()

    // Check tutor details
    composeTestRule.onNodeWithTag("selectedTutorDetailsScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorInfoRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorProfilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorAcademicInfo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorPrice").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorRatingLabel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorRatingIcon").assertIsDisplayed()

    // Check description
    composeTestRule.onNodeWithTag("tutorDescription").assertIsDisplayed()

    // Check reviews
    composeTestRule.onNodeWithTag("tutorReviewsSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorReviewsTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewCard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewComment").assertIsDisplayed()

    // Check confirmation button
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }

  @Test
  fun errorStateIsDisplayed_whenTutorNotTutor() {
    tutorProfileFlow.value = tutorProfileFlow.value.copy(role = Role.STUDENT)

    composeTestRule.setContent {
      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("errorStateColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
  }

  @Test
  fun emptyDescriptionIsDisplayed_whenTutorHasNoDescription() {
    tutorProfileFlow.value = tutorProfileFlow.value.copy(description = "")

    composeTestRule.setContent {
      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("tutorDescriptionSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorDescriptionEmpty").assertIsDisplayed()
  }

  @Test
  fun noReviewsSection_whenTutorHasNoCompletedLessons() {
    completedLessonsFlow.value = emptyList()

    composeTestRule.setContent {
      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("tutorReviewsSection").assertDoesNotExist()
  }

  @Test
  fun confirmationDialog_worksCorrectly() {
    composeTestRule.setContent {
      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Check dialog appears
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmDialogText").assertIsDisplayed()

    // Check confirmation works
    composeTestRule.onNodeWithTag("confirmDialogButton").performClick()
    verify(navigationActions).navigateTo(Screen.HOME)
    verify(lessonRepository).addLesson(any(), any(), any())

    // Check cancel works
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialogCancelButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialog").assertDoesNotExist()
  }
}
