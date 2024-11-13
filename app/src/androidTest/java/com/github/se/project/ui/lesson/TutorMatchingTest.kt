package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
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
              googleUid = "googleUid",
              firstName = "First",
              lastName = "Last",
              phoneNumber = "1234567890",
              role = Role.STUDENT,
              section = Section.GM,
              academicLevel = AcademicLevel.MA2,
              languages = listOf(),
              subjects = listOf(),
              schedule = listOf()))

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
              timeSlot = "2024-12-12T12:00",
              status = LessonStatus.MATCHING,
              latitude = 0.0,
              longitude = 0.0))

  private val tutorsFlow =
      MutableStateFlow(
          listOf(
              Profile(
                  uid = "tutor123",
                  googleUid = "googleUid",
                  firstName = "Tutor",
                  lastName = "One",
                  phoneNumber = "0987654321",
                  role = Role.TUTOR,
                  section = Section.IN,
                  academicLevel = AcademicLevel.MA1,
                  languages = listOf(Language.ENGLISH),
                  subjects = listOf(Subject.ANALYSIS),
                  schedule = listOf(),
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
      onSuccess(emptyList()) // Simulate returning an empty list of profiles
    }
  }

  @Test
  fun testNoTutorsMessageDisplayed_whenNoMatchingTutors() {
    // Set tutor list to empty
    tutorsFlow.value = emptyList()

    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the no tutor message is displayed
    composeTestRule.onNodeWithTag("noTutorMessage").assertIsDisplayed()
  }

  @Test
  fun testConfirmButton_whenNoTutorSelected() {
    composeTestRule.setContent {
      TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(Screen.HOME)
  }
}
