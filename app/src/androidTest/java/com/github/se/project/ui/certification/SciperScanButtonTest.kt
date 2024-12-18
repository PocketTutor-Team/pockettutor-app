import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.ui.certification.ScanningState
import com.github.se.project.ui.certification.SciperScanButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SciperScanButtonTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun whenClicked_showsCameraDialogAndFrame_inTestMode() {
    composeTestRule.setContent {
      SciperScanButton(
          onSciperCaptured = {},
          testMode = true,
          initialHasCameraPermission = true // Simulate that we already have permission
          )
    }

    // Initially, we should see the scan button
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").assertIsDisplayed()

    // Click the button to show the camera dialog
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").performClick()

    // Now the dialog should appear with scanning instructions
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please place the Camipro card within the frame.")
        .assertIsDisplayed()

    // The scanning frame should be visible
    composeTestRule.onNodeWithTag("ScanningFrame").assertIsDisplayed()
  }

  @Test
  fun showsDetectedState_inTestMode() {
    composeTestRule.setContent {
      SciperScanButton(
          onSciperCaptured = {},
          testMode = true,
          initialHasCameraPermission = true,
          initialShowCamera = true,
          initialScanningState = ScanningState.Detected)
    }

    // Since we set initialShowCamera and initialScanningState, we should see the detected state
    // immediately
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DetectedMessage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ScanningFrame").assertIsDisplayed()
  }

  @Test
  fun closeButton_closesDialogAndResetsState() {
    composeTestRule.setContent {
      SciperScanButton(
          onSciperCaptured = {},
          testMode = true,
          initialHasCameraPermission = true,
          initialShowCamera = true)
    }

    // Dialog should be visible
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertIsDisplayed()

    // Press the close button (X icon)
    composeTestRule.onNodeWithContentDescription("Close camera").performClick()

    // After closing, the dialog should no longer be displayed, only the main button
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Scan your Camipro Card").assertCountEquals(0)
  }

  @Test
  fun noDialogShown_beforeButtonClick() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}, testMode = true) }

    // Only the main scan button should be visible
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").assertIsDisplayed()

    // The dialog text should not be displayed yet
    composeTestRule.onAllNodesWithText("Scan your Camipro Card").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("ScanningFrame").assertCountEquals(0)
  }
}
