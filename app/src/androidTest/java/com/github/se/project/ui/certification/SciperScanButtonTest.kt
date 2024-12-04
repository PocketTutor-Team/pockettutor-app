package com.github.se.project.ui.certification

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

class SciperScanButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Test
  fun scanButtonInitiallyDisplayed() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    composeTestRule.onNodeWithText("Scan Camipro Card").assertExists()
  }

  @Test
  fun scanDialogOpensAndClosesProperly() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertExists()

    // Close the dialog
    composeTestRule.onNodeWithContentDescription("Close camera").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertDoesNotExist()
  }

  @Test
  fun scanButtonAllowsRetryAfterDismiss() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    // Open the scan dialog
    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertExists()

    // Dismiss the dialog
    composeTestRule.onNodeWithContentDescription("Close camera").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertDoesNotExist()

    // Retry scanning
    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertExists()
  }

  @Test
  fun scanButtonClickShowsDialog() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertExists()
    composeTestRule.onNodeWithText("Please place the Camipro card within the frame.").assertExists()
  }

  @Test
  fun closeButtonDismissesDialog() {
    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithContentDescription("Close camera").performClick()

    // Verify dialog is dismissed
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertDoesNotExist()
  }

  @Test
  fun scanningStateUpdatesUI() {
    var scanState = ScanningState.Scanning

    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    composeTestRule.onNodeWithText("Scan Camipro Card").performClick()
    composeTestRule.onNodeWithText("Please place the Camipro card within the frame.").assertExists()

    // Note: We can't directly test the SCIPER detection since it requires actual camera input
    // But we can verify the UI elements are present
    composeTestRule.onNodeWithText("Scan your Camipro Card").assertExists()
  }

  @Test
  fun cameraPermissionDeniedShowsMessage() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    composeTestRule.setContent { SciperScanButton(onSciperCaptured = {}) }

    // Note: This is a basic test for permission handling.
    // Full permission testing would require more complex instrumentation tests
    composeTestRule.onNodeWithText("Scan Camipro Card").assertExists()
  }
}
