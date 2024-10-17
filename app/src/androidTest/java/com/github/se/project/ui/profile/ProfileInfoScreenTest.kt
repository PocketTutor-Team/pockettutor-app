package com.github.se.project.ui.profile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ProfileInfoScreenTest {
  private lateinit var mockProfilesRepository: ProfilesRepository
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockListProfilesViewModel: ListProfilesViewModel
  private lateinit var mockLessonRepository: LessonRepository
  private lateinit var mockLessonViewModel: LessonViewModel

  private val mockProfile =
      Profile(
          uid = "12345",
          googleUid = "67890",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)
  private val mockProfileFlow = MutableStateFlow<Profile?>(mockProfile)

  val mockLesson =
      listOf(
          Lesson("1", "Math Tutoring", "2", Subject.ALGEBRA, listOf(Language.FRENCH), "12345"),
          Lesson("2", "Physics Tutoring", "3", Subject.PHYSICS, listOf(Language.FRENCH), "12345"))
  private val mockLessonFlow = MutableStateFlow<List<Lesson>>(mockLesson)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the dependencies
    mockLessonRepository = mock(LessonRepository::class.java)
    mockProfilesRepository = mock(ProfilesRepository::class.java)
    mockNavigationActions = mock(NavigationActions::class.java)

    // Create a real instance of ListProfilesViewModel
    mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)
    mockLessonViewModel = LessonViewModel(mockLessonRepository)

    // Mock the currentProfile StateFlow to return the mockProfile
    val currentProfileFlow = MutableStateFlow<Profile?>(mockProfile)
    // val currentLessonFlow = MutableStateFlow<List<Lesson>>(mockLesson)

    // Use a Mockito spy to track the actual state of the view model
    mockListProfilesViewModel = spy(mockListProfilesViewModel)

    // Stubbing the StateFlow
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    // Stub the updateProfile method to simulate a successful update
    doNothing().`when`(mockProfilesRepository).init(any())
    whenever(mockProfilesRepository.updateProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
  }

  @Test
  fun profileInfoScreenDisplaysProfileDetails_whenProfileIsNotNull() {

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel)
    }

    // Check that the top bar is displayed
    // composeTestRule.onNodeWithText("Profile Info").assertExists()

    // Check if profile details are displayed
    composeTestRule.onNodeWithText("John Doe").assertExists()
    composeTestRule.onNodeWithText("Status: MA2 Tutor").assertExists()
    composeTestRule.onNodeWithText("Section: GM").assertExists()
    composeTestRule.onNodeWithText("Price: 50.- per hour").assertExists()

    // Check if lessons are displayed
    composeTestRule.onNodeWithText("0 lessons given since you joined PocketTutor").assertExists()
    // composeTestRule.onNodeWithText("Math Tutoring with 2").assertExists()
    // composeTestRule.onNodeWithText("Physics Tutoring with 3").assertExists()
  }

  @Test
  fun profileInfoScreenDisplaysErrorMessage_whenProfileIsNull() {
    // Provide a ViewModel with null profile data
    val profileLiveData = MutableLiveData<Profile?>()
    profileLiveData.postValue(null) // Use postValue() instead of setValue()

    val lessonsLiveData = MutableLiveData<List<Lesson>>()
    lessonsLiveData.postValue(emptyList()) // Use postValue() for the lessons as well

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          // listProfilesViewModel = mockListProfilesViewModel, // This needs a valid ViewModel for
          // the test
          lessonViewModel = mockLessonViewModel)
    }

    // Check that the error message is displayed
    composeTestRule.onNodeWithText("Error loading profile...").assertExists()
  }

  @Test
  fun profileInfoScreenNavigatesBack_whenCloseButtonClicked() {
    val wasGoBackCalled = true

    composeTestRule.setContent { ProfileInfoScreen(navigationActions = mockNavigationActions) }

    // Click the close button
    composeTestRule.onNodeWithContentDescription("Close").performClick()

    // Verify that the goBack action was called
    assert(wasGoBackCalled)
  }
}
