package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class CreateTutorScheduleNoAccountTest {
  @get:Rule val composeTestRule = createComposeRule()

  private class FakeListProfilesViewModel :
      ListProfilesViewModel(mock(ProfilesRepository::class.java)) {
    private val _currentProfile = MutableStateFlow<Profile?>(null) // No profile set
    override val currentProfile = _currentProfile.asStateFlow()
  }

  @Test
  fun noProfileSelectedTest() {
    // Given
    val navigationActions = mock<NavigationActions>()
    val viewModel = FakeListProfilesViewModel()

    // Set content for the test
    composeTestRule.setContent { CreateTutorSchedule(navigationActions, viewModel) }

    // When the composable is set
    // Then
    composeTestRule
        .onNodeWithText("No Profile selected. Should not happen.", useUnmergedTree = true)
        .assertIsDisplayed()
  }
}
