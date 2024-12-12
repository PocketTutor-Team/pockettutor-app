package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FavoriteTutorTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: NavHostController

  private val profileFlow =
      MutableStateFlow(
          Profile(
              uid = "uid",
              token = "",
              googleUid = "googleUid",
              firstName = "First",
              lastName = "Last",
              phoneNumber = "1234567890",
              role = Role.STUDENT,
              section = Section.GM,
              academicLevel = AcademicLevel.MA2,
              favoriteTutors = listOf("tutor123"),
              languages = listOf(),
              subjects = listOf(),
              schedule = List(7) { List(12) { 1 } }))

  private val tutorsFlow =
      MutableStateFlow(
          listOf(
              Profile(
                  uid = "tutor123",
                  token = "",
                  googleUid = "googleUid",
                  firstName = "Tutor",
                  lastName = "One",
                  phoneNumber = "0987654321",
                  role = Role.TUTOR,
                  section = Section.IN,
                  academicLevel = AcademicLevel.MA1,
                  languages = listOf(Language.ENGLISH),
                  subjects = listOf(Subject.ANALYSIS),
                  schedule = List(7) { List(12) { 1 } },
                  price = 30)))

  @Before
  fun setup() {
    // Mock dependencies
    profilesRepository = mock(ProfilesRepository::class.java)
    navController = mock()

    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
    listProfilesViewModel = spy(listProfilesViewModel)

    navigationActions = NavigationActions(navController)
    navigationActions = spy(navigationActions)

    // Stub navigation actions
    doNothing().`when`(navigationActions).navigateTo(anyString())
    doNothing().`when`(navigationActions).goBack()

    // Mock flow properties on ViewModels
    whenever(listProfilesViewModel.currentProfile).thenReturn(profileFlow)

    // Stub repository methods to simulate successful data retrieval
    whenever(profilesRepository.init(any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as () -> Unit
      onSuccess() // Simulate successful initialization
    }

    whenever(profilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(tutorsFlow.value) // Simulate returning an empty list of profiles
    }

    listProfilesViewModel.getProfiles()
  }

  @Test
  fun testNoTutorsMessageDisplayed_whenNoFavoriteTutors() {
    // Set tutor list to empty
    profileFlow.value.favoriteTutors = emptyList()
    listProfilesViewModel.getProfiles()

    composeTestRule.setContent { FavoriteTutorsScreen(listProfilesViewModel, navigationActions) }

    // Verify the no tutor message is displayed
    composeTestRule.onNodeWithTag("noTutorMessage").assertIsDisplayed()
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent { FavoriteTutorsScreen(listProfilesViewModel, navigationActions) }

    // Verify the top bar is correctly displayed
    composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BookmarkedTutorsTitle").assertIsDisplayed()

    // Verify the tutor list is displayed
    composeTestRule.onNodeWithTag("tutorsList").assertIsDisplayed()
  }

  @Test
  fun selectAFavoriteTutor() {
    composeTestRule.setContent { FavoriteTutorsScreen(listProfilesViewModel, navigationActions) }

    // Select a tutor
    composeTestRule.onNodeWithTag("tutorsList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorCard_0").assertIsDisplayed().performClick()

    // Verify the user is navigated to the selected tutor screen
    verify(navigationActions).navigateTo(Screen.FAVORITE_TUTOR_DETAILS)
  }

  @Test
  fun goBackButton_navigateBack() {
    composeTestRule.setContent { FavoriteTutorsScreen(listProfilesViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed().performClick()
    verify(navigationActions).goBack()
  }
}
