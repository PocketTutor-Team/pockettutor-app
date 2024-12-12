package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.authentification.AuthenticationViewModel
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.EpflVerificationRepository
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
import com.github.se.project.ui.navigation.Screen
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
  private lateinit var realAuthenticationViewModel: AuthenticationViewModel
  private lateinit var certificationViewModel: CertificationViewModel
  private lateinit var certificationRepository: EpflVerificationRepository

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
    // Initialize mocks
    mockLessonRepository = mock(LessonRepository::class.java)
    mockProfilesRepository = mock(ProfilesRepository::class.java)
    mockNavigationActions = mock(NavigationActions::class.java)
    realAuthenticationViewModel = AuthenticationViewModel()
    certificationRepository = mock(EpflVerificationRepository::class.java)

    // Create the view models after repositories are ready
    mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)
    mockLessonViewModel = LessonViewModel(mockLessonRepository)

    // Spy on mockListProfilesViewModel if needed
    mockListProfilesViewModel = spy(mockListProfilesViewModel)

    // Initialize CertificationViewModel
    certificationViewModel =
        CertificationViewModel(certificationRepository, mockListProfilesViewModel)

    // Set up repository behavior
    doNothing().`when`(mockProfilesRepository).init(any())
    whenever(mockProfilesRepository.updateProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
  }

  @Test
  fun profileInfoScreen_everythingDisplayedCorrectly() {
    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule.onNodeWithTag("profileTopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("closeButton").assertIsDisplayed()
  }

  @Test
  fun tutorProfileInfoScreenDisplaysProfileDetails_whenProfileIsNotNull() {
    val currentProfileFlow = MutableStateFlow<Profile?>(mockTutorProfile)
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule
        .onNodeWithTag("profileName")
        .assertIsDisplayed()
        .assertTextEquals("${mockTutorProfile.firstName} ${mockTutorProfile.lastName}")

    composeTestRule
        .onNodeWithTag("profileAcademicInfo")
        .assertIsDisplayed()
        .assertTextEquals("${mockTutorProfile.section} - ${mockTutorProfile.academicLevel}")

    composeTestRule.onNodeWithTag("lessonsCount").assertIsDisplayed()

    composeTestRule.onNodeWithTag("priceText").assertIsDisplayed()
  }

  @Test
  fun studentProfileInfoScreenDisplaysProfileDetails_whenProfileIsNotNull() {
    val currentProfileFlow = MutableStateFlow<Profile?>(mockStudentProfile)
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule
        .onNodeWithTag("profileName")
        .assertIsDisplayed()
        .assertTextEquals(
            "${mockStudentProfile.firstName.capitalizeFirstLetter()} ${mockStudentProfile.lastName.capitalizeFirstLetter()}")
    composeTestRule
        .onNodeWithTag("profileAcademicInfo")
        .assertIsDisplayed()
        .assertTextEquals("${mockStudentProfile.section} - ${mockStudentProfile.academicLevel}")

    composeTestRule.onNodeWithTag("lessonsCount").assertIsDisplayed()
  }

  @Test
  fun profileInfoScreenDisplaysErrorMessage_whenProfileIsNull() {
    val profileLiveData = MutableLiveData<Profile?>()
    profileLiveData.postValue(null)

    val lessonsLiveData = MutableLiveData<List<Lesson>>()
    lessonsLiveData.postValue(emptyList())

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule
        .onNodeWithTag("errorLoadingProfile")
        .assertIsDisplayed()
        .assertTextEquals("Error loading profile…")
  }

  @Test
  fun profileInfoScreenNavigatesBack_whenCloseButtonClicked() {
    `when`(mockNavigationActions.goBack()).thenAnswer {}

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule.onNodeWithTag("closeButton").assertIsDisplayed().performClick()

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun logOutButton() {
    val currentProfileFlow = MutableStateFlow<Profile?>(mockTutorProfile)
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile

    realAuthenticationViewModel.userId.postValue("12345")

    composeTestRule.setContent {
      ProfileInfoScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          lessonViewModel = mockLessonViewModel,
          authenticationViewModel = realAuthenticationViewModel,
          certificationViewModel = certificationViewModel)
    }

    composeTestRule.onNodeWithTag("signOutButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signOutButton").performClick()

    assert(realAuthenticationViewModel.userId.value == null)
    verify(mockNavigationActions).navigateTo(Screen.AUTH)
  }
}
