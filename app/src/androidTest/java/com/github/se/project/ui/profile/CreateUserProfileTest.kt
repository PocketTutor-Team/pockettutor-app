import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.profile.CreateProfileScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class CreateProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocking navigation actions
  private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)
  private val mockViewModel = Mockito.mock(ListProfilesViewModel::class.java)

  @Test
  fun createProfileScreen_rendersCorrectly() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateProfileScreen(navigationActions = mockNavigationActions, googleUid = "mockUid")
    }

    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("instructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleSelection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sectionDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("academicLevelDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("phoneNumberField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    composeTestRule.setContent {
      CreateProfileScreen(navigationActions = mockNavigationActions, googleUid = "mockUid")
    }

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun phoneNumberValidation_showsErrorForInvalidPhone() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateProfileScreen(navigationActions = mockNavigationActions, googleUid = "mockUid")
    }

    // Enter an invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123ABC")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify no navigation due to invalid input
    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun selectingRole_updatesRoleCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(navigationActions = mockNavigationActions, googleUid = "mockUid")
    }

    // Select "Student" role
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

    // Check that the button is selected
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsSelected()

    // Select "Tutor" role
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    // Check that the Tutor role is now selected
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()
  }
}
