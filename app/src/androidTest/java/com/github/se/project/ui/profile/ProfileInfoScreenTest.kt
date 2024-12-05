package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.utils.capitalizeFirstLetter
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ProfileInfoScreenTest {
  private lateinit var mockProfilesRepository: ProfilesRepository
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockListProfilesViewModel: ListProfilesViewModel
  private lateinit var mockLessonRepository: LessonRepository
  private lateinit var mockLessonViewModel: LessonViewModel

  private val mockTutorProfile =
      Profile(
          uid = "12345",
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

  private val mockStudentProfile =
      Profile(
          uid = "1",
          token = "",
          googleUid = "1",
          firstName = "Student",
          lastName = "Test",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2)

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

    // Use a Mockito spy to track the actual state of the view model
    mockListProfilesViewModel = spy(mockListProfilesViewModel)

    // Stub the updateProfile method to simulate a successful update
    doNothing().`when`(mockProfilesRepository).init(any())
    whenever(mockProfilesRepository.updateProfile(any(), any(), any())).thenAnswer { invocation ->
        @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
  }

  // PASS
  @Test
  fun profileInfoScreen_everythingDisplayedCorrectly() {
    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel)
    }

    composeTestRule.onNodeWithTag("profileTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("closeButton").assertIsDisplayed()
  }

  // PASS
  @Test
  fun tutorProfileInfoScreenDisplaysProfileDetails_whenProfileIsNotNull() {
    // Mock the currentProfile StateFlow to return the mockProfile
    val currentProfileFlow = MutableStateFlow<Profile?>(mockTutorProfile)

    // Stubbing the StateFlow
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel)
    }

    // Check if profile details are displayed
    composeTestRule
        .onNodeWithTag("profileName")
        .assertIsDisplayed()
        .assertTextEquals("${mockTutorProfile.firstName} ${mockTutorProfile.lastName}")
    composeTestRule
        .onNodeWithTag("profileAcademicInfo")
        .assertIsDisplayed()
        .assertTextEquals("${mockTutorProfile.section} - ${mockTutorProfile.academicLevel}")
    composeTestRule
        .onNodeWithTag("lessonsCount")
        .assertIsDisplayed()
        .assertTextEquals("0 lessons given since you joined PocketTutor")
    composeTestRule
        .onNodeWithTag("priceText")
        .assertIsDisplayed()
        .assertTextEquals("Price: ${mockTutorProfile.price}")
    composeTestRule
        .onNodeWithTag("phoneNumberRow")
        .assertIsDisplayed()
        .assertTextEquals(mockTutorProfile.phoneNumber)
  }

  @Test
  fun studentProfileInfoScreenDisplaysProfileDetails_whenProfileIsNotNull() {
    // Mock the currentProfile StateFlow to return the mockProfile
    val currentProfileFlow = MutableStateFlow<Profile?>(mockStudentProfile)

    // Stubbing the StateFlow
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel)
    }

    // Check if profile details are displayed
    composeTestRule
        .onNodeWithTag("profileName")
        .assertIsDisplayed()
        .assertTextEquals(
            "${mockStudentProfile.firstName.capitalizeFirstLetter()} ${mockStudentProfile.lastName.capitalizeFirstLetter()}")
    composeTestRule
        .onNodeWithTag("profileAcademicInfo")
        .assertIsDisplayed()
        .assertTextEquals("${mockStudentProfile.section} - ${mockStudentProfile.academicLevel}")
    composeTestRule
        .onNodeWithTag("lessonsCount")
        .assertIsDisplayed()
        .assertTextEquals("0 lessons taken since you joined PocketTutor")
  }

  // PASS
  @Test
  fun profileInfoScreenDisplaysErrorMessage_whenProfileIsNull() {
    // Provide a ViewModel with null profile data
    val profileLiveData = MutableLiveData<Profile?>()
    profileLiveData.postValue(null) // Use postValue() instead of setValue()

    val lessonsLiveData = MutableLiveData<List<Lesson>>()
    lessonsLiveData.postValue(emptyList()) // Use postValue() for the lessons as well

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions, lessonViewModel = mockLessonViewModel)
    }

    // Check that the error message is displayed
    composeTestRule
        .onNodeWithTag("errorLoadingProfile")
        .assertIsDisplayed()
        .assertTextEquals("Error loading profile...")
  }

  // Pass
  @Test
  fun profileInfoScreenNavigatesBack_whenCloseButtonClicked() {
    `when`(mockNavigationActions.goBack()).thenAnswer {}

    composeTestRule.setContent { ProfileInfoScreen(navigationActions = mockNavigationActions) }

    // Click the close button
    composeTestRule.onNodeWithTag("closeButton").assertIsDisplayed().performClick()

    // Verify that the goBack action was called
    verify(mockNavigationActions).goBack()
  }
}
