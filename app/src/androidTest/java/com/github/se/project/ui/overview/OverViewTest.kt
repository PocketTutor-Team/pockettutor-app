package com.github.se.project.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.chat.ChatViewModel
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
import com.github.se.project.ui.navigation.Route
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class HomeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val grantNotificationPermission: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private lateinit var chatViewModel: ChatViewModel
  private lateinit var chatClient: ChatClient

  private lateinit var navController: NavHostController
  private lateinit var navigationActions: NavigationActions

  private val tutorProfile =
      Profile(
          uid = "tutor",
          token = "",
          googleUid = "googleUid",
          firstName = "firstName",
          lastName = "lastName",
          phoneNumber = "phoneNumber",
          role = Role.TUTOR,
          section = Section.AR,
          academicLevel = AcademicLevel.BA1,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 0)

  private val studentProfile =
      Profile(
          uid = "student",
          token = "",
          googleUid = "googleUid",
          firstName = "firstName",
          lastName = "lastName",
          phoneNumber = "phoneNumber",
          role = Role.STUDENT,
          section = Section.AR,
          academicLevel = AcademicLevel.BA1,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 0)

  private val mockLessons =
      mutableListOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor"),
              studentUid = "student",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T10:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor"),
              studentUid = "student",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T11:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0))

  private val instantLesson =
      Lesson(
          id = "3",
          title = "Instant ICC Tutoring",
          description = "Algebra and Calculus",
          subject = Subject.ICC,
          languages = listOf(Language.ENGLISH),
          tutorUid = listOf("tutor123"),
          studentUid = "student123",
          minPrice = 20.0,
          maxPrice = 40.0,
          timeSlot = "2024-10-10Tinstant",
          status = LessonStatus.INSTANT_CONFIRMED,
          latitude = 0.0,
          longitude = 0.0)

  private val mockstudentProfileFlow = MutableStateFlow<Profile?>(tutorProfile)

  private val cancelledLesson =
      listOf(
          Lesson(
              id = "3",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor"),
              studentUid = "student",
              minPrice = 20.0,
              maxPrice = 40.0,
              price = 25.0,
              timeSlot = "2024-10-10T11:00:00",
              status = LessonStatus.STUDENT_CANCELLED,
              latitude = 0.0,
              longitude = 0.0))

  private val mockProfileFlow = MutableStateFlow<Profile?>(tutorProfile)

  private val currentUserLessonsFlow = MutableStateFlow(mockLessons)

  private val cancelledLessonsFlow = MutableStateFlow<List<Lesson>>(listOf())

  @Before
  fun setup() {
    /// Mock the dependencies
    profilesRepository = mock(ProfilesRepository::class.java)
    lessonRepository = mock(LessonRepository::class.java)
    navController = mock()

    // Initialize the ViewModel with a spy
    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
    listProfilesViewModel = spy(listProfilesViewModel)

    lessonViewModel = LessonViewModel(lessonRepository)
    lessonViewModel = spy(lessonViewModel)

    // Mock ChatViewModel
    chatViewModel = mock(ChatViewModel::class.java)
    doNothing().`when`(chatViewModel).connect(any())

    navigationActions = NavigationActions(navController)
    navigationActions = spy(navigationActions)

    // Stub the methods in NavigationActions
    doNothing().`when`(navigationActions).navigateTo(anyString())
    `when`(navigationActions.currentRoute()).thenReturn(Route.HOME)

    // Set up the StateFlows
    doReturn(mockProfileFlow).`when`(listProfilesViewModel).currentProfile
    doReturn(currentUserLessonsFlow).`when`(lessonViewModel).currentUserLessons
    doReturn(cancelledLessonsFlow).`when`(lessonViewModel).cancelledLessons

    // Stub the init and getProfiles method in the repository to simulate success
    doNothing().`when`(profilesRepository).init(any())
    whenever(profilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(tutorProfile, studentProfile)) // Simulate a list of profiles
    }
  }

  @Test
  fun testIsDisplayed() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
  }

  @Test
  fun testProfileIconClickable() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }
    composeTestRule.onNodeWithContentDescription("Profile Icon").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
  }

  @Test
  fun testSectionDisplays() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }
    composeTestRule.onNodeWithText("Waiting for your Confirmation").assertIsDisplayed()
    composeTestRule.onNodeWithText("Waiting for the Student Confirmation").assertIsDisplayed()
    composeTestRule.onNodeWithText("Upcoming Lessons").assertIsDisplayed()
    composeTestRule.onNodeWithText("Upcoming Instant Lesson").assertIsNotDisplayed()
  }

  @Test
  fun testNoProfileFoundScreenDisplayed() {
    // Set a null profile in the ViewModel
    listProfilesViewModel =
        mock(ListProfilesViewModel::class.java).apply {
          `when`(currentProfile).thenReturn(MutableStateFlow(null))
        }
    // Recompose
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }

    // Verify that the "No profile is currently assigned" text is displayed
    composeTestRule
        .onNodeWithText("No profile is currently assigned to the current user.")
        .assertIsDisplayed()

    // Verify the button to go back to HOME screen is displayed
    composeTestRule.onNodeWithText("Go back to HOME screen").assertIsDisplayed()
  }

  @Test
  fun testLessonsEmptyMessageDisplayed() {
    // Set up empty lessons scenario
    val mockRepository = mock<LessonRepository>()
    currentUserLessonsFlow.value = mutableListOf() // Simulate no lessons
    lessonViewModel =
        spy(LessonViewModel(mockRepository)).apply {
          // Assuming currentUserLessons is initialized in the constructor or some method,
          // you'll have to use reflection to set it if it's private or internal.
          `when`(this.currentUserLessons).thenReturn(currentUserLessonsFlow)
        }
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }

    // Verify the message indicating no lessons scheduled is displayed
    composeTestRule.onNodeWithTag("noLessonsText").assertIsDisplayed()
  }

  @Test
  fun testInstantDisplay() {
    mockLessons.add(instantLesson)
    currentUserLessonsFlow.value = mockLessons

    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }

    composeTestRule.onNodeWithText("Instant ICC Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Upcoming Instant Lesson").assertIsDisplayed()
  }

  @Test
  fun testCancelledLessonsDisplayed() {
    cancelledLessonsFlow.value = cancelledLesson

    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }

    // Verify the dialog indicating that the lesson has been cancelled is displayed
    composeTestRule.onNodeWithTag("cancelledLessonDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelledLessonDialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelledLessonDialogText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelledLessonDialogConfirmButton").assertIsDisplayed()
  }

  @Test
  fun testEmptySectionTextDisplayed() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, chatViewModel, navigationActions)
    }
    composeTestRule.onNodeWithText("Upcoming Lessons").assertIsDisplayed()
    composeTestRule.onNodeWithTag("section_CONFIRMED_expand").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("noLessonsConfirmed").assertIsDisplayed()
  }
}
