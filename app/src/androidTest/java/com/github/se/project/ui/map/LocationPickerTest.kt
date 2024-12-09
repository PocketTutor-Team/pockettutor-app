package com.github.se.project.ui.map

import LocationPickerBox
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MapPickerBoxTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mapPickerBox_withInitialLocation_displaysCorrectly_andConfirmButtonTriggersCallback() {
    // Given initial non-zero coordinates for the test
    val initialLatitude = 46.5
    val initialLongitude = 6.6
    var selectedLocation: Pair<Double, Double>? = null

    // Set the content with MapPickerBox
    composeTestRule.setContent {
      LocationPickerBox(
          initialLocation = initialLatitude to initialLongitude,
          onLocationSelected = { location -> selectedLocation = location },
          onMapReady = { _ -> })
    }

    // Check if the map itself is displayed
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

    // Check if the helper text displays the correct message
    composeTestRule.onNodeWithTag("helperText").assertIsDisplayed()

    // Check if the confirm button is displayed and enabled
    composeTestRule.onNodeWithTag("confirmLocation").assertIsDisplayed().assertIsEnabled()

    // Tap the confirm button
    composeTestRule.onNodeWithTag("confirmLocation").performClick()

    // Verify the callback was triggered with the correct location
    assertEquals(initialLatitude to initialLongitude, selectedLocation)
  }
}
