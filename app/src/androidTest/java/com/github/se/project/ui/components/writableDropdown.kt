package com.github.se.project.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class WritableDropdownTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testWritableDropdownRenders() {
      composeTestRule.setContent {
          WritableDropdown(
              label = "Select Item",
              placeholder = "Choose an option",
              value = "",
              onValueChange = {},
              choices = listOf("Apple", "Banana", "Cherry")
          )
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
              choices = listOf("Apple", "Banana", "Cherry")
          )
      }

      composeTestRule.onNodeWithText("Select Item").performClick()
      // Enter a value
      composeTestRule.onNodeWithText("Choose an option").performTextInput("A")

      // Check that the value is updated
      assert(currentValue == "A")
  }

  @Test
  fun testDropdownDisplaysFilteredChoices() {
      var currentValue = ""
      composeTestRule.setContent {
          WritableDropdown(
              label = "Select Item",
              placeholder = "Choose an option",
              value = currentValue,
              onValueChange = { currentValue = it },
              choices = listOf("Apple", "Banana", "Cherry")
          )
      }

      // Enter a value and open the dropdown
      composeTestRule.onNodeWithText("Select Item").performClick()
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithText("Choose an option").performTextInput("a")
      composeTestRule.waitForIdle()
      // Check that the filtered dropdown displays only the correct choices
      composeTestRule.onNodeWithTag("item_Apple").assertIsDisplayed()
      composeTestRule.onNodeWithText("Banana").assertIsNotDisplayed()
      composeTestRule.onNodeWithText("Cherry").assertIsNotDisplayed()
  }

  @Test
  fun testSelectionFromDropdownUpdatesTextField() {
      var currentValue = ""
      composeTestRule.setContent {
          WritableDropdown(
              label = "Select Item",
              placeholder = "Choose an option",
              value = currentValue,
              onValueChange = { currentValue = it },
              choices = listOf("Apple", "Banana", "Cherry")
          )
      }

      // Type a character to open the dropdown
      composeTestRule.onNodeWithText("Select Item").performClick()
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithText("Choose an option").performTextInput("A")
      composeTestRule.waitForIdle()

      // Click on the dropdown item
      composeTestRule.onNodeWithTag("item_Apple").performClick()
      composeTestRule.waitForIdle()

      // Verify that the text field now has the selected value
      assert(currentValue == "Apple")
  }
}
