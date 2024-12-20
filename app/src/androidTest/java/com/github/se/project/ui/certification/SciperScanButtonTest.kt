import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.ui.certification.SciperScanButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SciperScanButtonTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun whenClicked_showsCameraDialogAndFrame_inTestMode() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}, testMode = true) }

    // Initially, we should see the scan button
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").assertIsDisplayed()

    // Click the button to show the camera dialog
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").performClick()

    // Wait for the UI to settle after the click
    composeTestRule.waitForIdle()

    // Now the dialog should appear with scanning instructions
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Please place the Camipro card within the frame.")
        .assertIsDisplayed()

    // The scanning frame should be visible
    composeTestRule.onNodeWithTag("ScanningFrame").assertIsDisplayed()
  }

  @Test
  fun closeButton_closesDialogAndResetsState() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}, testMode = true) }

    // Show the camera dialog by clicking the button
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").assertIsDisplayed()
    composeTestRule.onNodeWithText("Scan Camipro Card (Test)").performClick()

    // Dialog should now be visible
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
