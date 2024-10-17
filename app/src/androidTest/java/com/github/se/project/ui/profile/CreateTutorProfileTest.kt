package com.github.se.project.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import java.util.EnumSet
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class CreateTutorProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocking navigation actions
  private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)
  private val mockViewModel =
      Mockito.mock(ListProfilesViewModel::class.java).apply {
        Mockito.`when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(null))
      }
  // list of languages
  private val languages = Language.entries.toTypedArray()
  private val subjects = Subject.entries.toTypedArray()

  @Test
  fun createTutorProfileScreen_rendersCorrectly() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("instructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorInfoScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("subjectText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceSlider").assertIsDisplayed()
    composeTestRule.onNodeWithTag("languageSelection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("subjectBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("subjectButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("languageText").assertIsDisplayed()
    languages.forEach { language ->
      composeTestRule.onNodeWithTag("${language}Text").assertIsDisplayed()
    }
    languages.forEach { language ->
      composeTestRule.onNodeWithTag("${language}Text").assertIsDisplayed()
    }
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun selectingLanguagesUpdatesCorrectly() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    languages.forEach { language ->
      // Check that the language is not selected
      composeTestRule.onNodeWithTag("${language}Check").assertIsOff()

      // Select the language
      composeTestRule.onNodeWithTag("${language}Check").performClick()

      // Check that the language is now selected
      composeTestRule.onNodeWithTag("${language}Check").assertIsOn()
    }
  }

  @Test
  fun selectingSubjectsUpdatesCorrectly() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    subjects.forEach { subject ->
      composeTestRule.onNodeWithTag("${subject.name}Text").isNotDisplayed()
      composeTestRule.onNodeWithTag("dropdown${subject.name}").isNotDisplayed()
    }
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    subjects.forEach { subject ->
      // composeTestRule.onNodeWithTag("${subject}Text").assertIsDisplayed()
      composeTestRule.onNodeWithTag("dropdown${subject.name}").assertIsDisplayed()
    }

    subjects.forEach { subject ->
      // Check that the subject is not selected
      composeTestRule.onNodeWithTag("${subject.name}Checkmark").assertIsNotDisplayed()

      // Select the subject
      composeTestRule.onNodeWithTag("dropdown${subject.name}").performClick()

      // Check that the subject is now selected
      composeTestRule
          .onNodeWithTag("${subject.name}Checkmark", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }

  /*@Test
  fun languageAndSubjectNotSelectedShowsToast() {
      (mockViewModel.currentProfile as MutableStateFlow).value =
          Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
              role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
              languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(Subject::class.java), schedule = listOf())
      // Set the screen in the test environment
      composeTestRule.setContent {
          CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
      }

      composeTestRule.onNodeWithTag("confirmButton").performClick()

      Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }*/

  /*@Test
  fun languageAndSubjectSelectedNavigatesAway() {
      (mockViewModel.currentProfile as MutableStateFlow).value =
          Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
              role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
              languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(Subject::class.java), schedule = listOf())
      // Set the screen in the test environment
      composeTestRule.setContent {
          CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
      }

      val sampleProfile = Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
          role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
          languages = EnumSet.of(languages[0]), subjects = EnumSet.of(subjects[0]), schedule = listOf())

      val sample2 = Profile(uid = "1", googleUid = "googleUid", firstName = "First", lastName = "Last", phoneNumber = "1234567890",
          role = Role.STUDENT, section = Section.GM, academicLevel = AcademicLevel.MA2,
          languages = EnumSet.noneOf(Language::class.java), subjects = EnumSet.noneOf(Subject::class.java), schedule = listOf())

      // Stub the updateProfile method
      `when`(mockViewModel.updateProfile(any())).thenAnswer {}

      composeTestRule.onNodeWithTag("${languages[0]}Check").performClick()
      composeTestRule.onNodeWithTag("subjectButton").performClick()
      composeTestRule.onNodeWithTag("dropdown${subjects[0].name}").performClick()

      composeTestRule.onNodeWithTag("confirmButton").performClick()
      //assert(mockViewModel.currentProfile.value != null)
      //assert(mockViewModel.currentProfile.value!!.subjects.isNotEmpty())
      Mockito.verify(mockNavigationActions).navigateTo(Screen.CREATE_TUTOR_SCHEDULE)
  }*/

  @Test
  fun sliderTextTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("priceDifferenceLow").assertIsDisplayed()

    // Perform the sliding action on the slider
    composeTestRule.onNodeWithTag("priceSlider").performGesture { swipeRight() }

    // Verify the slider's value has changed
    composeTestRule.onNodeWithTag("priceDifferenceHigh").assertIsDisplayed()
  }

  @Test
  fun noProfileTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value = null
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory))
    }

    // Assert
    composeTestRule
        .onNodeWithTag("noProfile")
        .assertTextEquals("No Profile selected. Should not happen.")
  }

  @Test
  fun doubleClickTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("${languages[0]}Check").performClick()
    composeTestRule.onNodeWithTag("${languages[0]}Check").performClick()
    composeTestRule.onNodeWithTag("${languages[0]}Check").assertIsOff()

    composeTestRule.onNodeWithTag("subjectButton").performClick()

    subjects.forEach { subject ->
      composeTestRule.onNodeWithTag("dropdown${subject.name}").performClick()
      composeTestRule.onNodeWithTag("dropdown${subject.name}").performClick()
      composeTestRule.onNodeWithTag("${subject.name}Checkmark").assertIsNotDisplayed()
    }
  }
}

class MockProfilesRepository : ProfilesRepository {
  private val profiles = mutableListOf<Profile>()

  override fun init(onSuccess: () -> Unit) {
    onSuccess()
  }

  override fun getNewUid(): String {
    return "mockUid"
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    onSuccess(profiles)
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    profiles.add(profile)
    onSuccess()
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val index = profiles.indexOfFirst { it.uid == profile.uid }
    if (index != -1) {
      profiles[index] = profile
      onSuccess()
    } else {
      onFailure(Exception("Profile not found"))
    }
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    onSuccess() // This not deleting anything is not a problem: this class is only used to test
    // adding profiles
  }
}
