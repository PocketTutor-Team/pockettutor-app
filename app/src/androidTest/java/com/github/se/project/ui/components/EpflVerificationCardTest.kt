import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.certification.CertificationViewModel.VerificationState
import com.github.se.project.model.certification.VerificationResult
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.components.EpflVerificationCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EpflVerificationCardTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun defaultState_showsInputAndScanButton() {
    // Given a default state (no error, no success, no loading)
    composeTestRule.setContent {
      EpflVerificationCard(
          sciper = "",
          onSciperChange = {},
          verificationState = VerificationState.Idle,
          onVerifyClick = {},
          onResetVerification = {})
    }

    // Check that instructions are displayed
    composeTestRule.onNodeWithText("EPFL Verification").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "Verify your EPFL status by scanning your Camipro card or entering your SCIPER number.")
        .assertIsDisplayed()

    // Check that the OutlinedTextField is displayed
    composeTestRule.onNodeWithText("SCIPER Number").assertIsDisplayed()

    // Check that SciperScanButton is displayed
    composeTestRule.onNodeWithText("Scan Camipro Card").assertIsDisplayed()
  }

  @Test
  fun whenSciperIsSixDigits_showsVerifyButton() {
    var sciperValue = ""
    val onSciperChange: (String) -> Unit = { sciperValue = it }

    composeTestRule.setContent {
      EpflVerificationCard(
          sciper = "123456",
          onSciperChange = onSciperChange,
          verificationState = VerificationState.Idle,
          onVerifyClick = {},
          onResetVerification = {})
    }

    // OutlinedTextField should have a trailing icon (verify send button)
    composeTestRule.onNodeWithContentDescription("Verify").assertIsDisplayed()
  }

  @Test
  fun successState_showsVerifiedInfo_andResetButton() {
    // Mock a successful verification result
    val successResult =
        VerificationResult.Success(
            firstName = "John",
            lastName = "Doe",
            section = Section.IN,
            academicLevel = AcademicLevel.MA2)

    val mockProfile = VerificationState.Success(result = successResult)

    var resetClicked = false
    val onResetVerification = { resetClicked = true }

    composeTestRule.setContent {
      EpflVerificationCard(
          sciper = "",
          onSciperChange = {},
          verificationState = mockProfile,
          onVerifyClick = {},
          onResetVerification = onResetVerification)
    }

    // Check that success message is shown
    composeTestRule.onNodeWithText("Verified as John Doe").assertIsDisplayed()
    composeTestRule.onNodeWithText("IN - MA2").assertIsDisplayed()

    // Check that the reset button is displayed and clickable
    composeTestRule
        .onNodeWithContentDescription("Reset verification")
        .assertIsDisplayed()
        .performClick()
    composeTestRule.runOnIdle { assert(resetClicked) }

    // Input fields should not be visible in success state
    composeTestRule.onAllNodesWithText("Verify your EPFL status by scanning").assertCountEquals(0)
    composeTestRule.onAllNodesWithText("SCIPER Number").assertCountEquals(0)
  }

  @Test
  fun errorState_showsErrorMessage() {
    val errorMessage = "Invalid SCIPER"
    val verificationState =
        VerificationState.Error(
            error = VerificationResult.Error.InvalidSciper, message = errorMessage)

    composeTestRule.setContent {
      EpflVerificationCard(
          sciper = "123456", // sciper has 6 chars, so verify button should appear
          onSciperChange = {},
          verificationState = verificationState,
          onVerifyClick = {},
          onResetVerification = {})
    }

    // Check for error message
    composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()

    // Verify button should still be displayed
    composeTestRule.onNodeWithContentDescription("Verify").assertIsDisplayed()
  }

  @Test
  fun loadingState_showsProgressIndicator() {
    composeTestRule.setContent {
      EpflVerificationCard(
          sciper = "",
          onSciperChange = {},
          verificationState = VerificationState.Loading,
          onVerifyClick = {},
          onResetVerification = {})
    }

    // Check that the progress indicator is displayed
    composeTestRule.onNodeWithTag("progressIndicator").assertIsDisplayed()
  }
}
