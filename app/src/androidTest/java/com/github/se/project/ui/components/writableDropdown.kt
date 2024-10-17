package com.github.se.project.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class WritableDropdownTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testWritableDropdownRenders() {
    composeTestRule.setContent {
      WritableDropdown(
          label = "Select Item",
          placeholder = "Choose an option",
          value = "",
          onValueChange = {},
          choices = listOf("Apple", "Banana", "Cherry"))
    }

    // Check that the label and placeholder are displayed
    composeTestRule.onNodeWithText("Select Item").assertIsDisplayed()
    composeTestRule.onNodeWithText("Select Item").performClick()
    composeTestRule.onNodeWithText("Choose an option").assertIsDisplayed()
  }

  @Test
  fun testUserInputUpdatesValue() {
    var currentValue = ""
    composeTestRule.setContent {
      WritableDropdown(
          label = "Select Item",
          placeholder = "Choose an option",
          value = currentValue,
          onValueChange = { currentValue = it },
          choices = listOf("Apple", "Banana", "Cherry"))
    }

    composeTestRule.onNodeWithText("Select Item").performClick()
    // Enter a value
    composeTestRule.onNodeWithText("Choose an option").performTextInput("A")

    // Check that the value is updated
    assert(currentValue == "A")
  }

  //  @Test
  //  fun testDropdownDisplaysFilteredChoices() {
  //      var currentValue = ""
  //      composeTestRule.setContent {
  //          WritableDropdown(
  //              label = "Select Item",
  //              placeholder = "Choose an option",
  //              value = currentValue,
  //              onValueChange = { currentValue = it },
  //              choices = listOf("Apple", "Banana", "Cherry")
  //          )
  //      }
  //
  //      // Enter a value and open the dropdown
  //      composeTestRule.onNodeWithText("Select Item").performClick()
  //      composeTestRule.onNodeWithText("Choose an option").performTextInput("A")
  //
  //      // Check that the filtered dropdown displays only the correct choices
  //      composeTestRule.onNodeWithText("Apple").assertIsDisplayed()
  //      composeTestRule.onNodeWithText("Banana").assertDoesNotExist()
  //      composeTestRule.onNodeWithText("Cherry").assertDoesNotExist()
  //  }

  /*@Test
  fun testSelectionFromDropdownUpdatesTextField() {
    var currentValue = ""
    composeTestRule.setContent {
      WritableDropdown(
          label = "Select Item",
          placeholder = "Choose an option",
          value = currentValue,
          onValueChange = { currentValue = it },
          choices = listOf("Apple", "Banana", "Cherry"))
    }

    // Type a character to open the dropdown
    composeTestRule.onNodeWithText("Select Item").performClick()
    composeTestRule.onNodeWithText("Choose an option").performTextInput("A")

    // Click on the dropdown item
    composeTestRule.onNodeWithText("Apple").performClick()

    // Verify that the text field now has the selected value
    assert(currentValue == "Apple")
  }*/
}
