package com.github.se.project.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

  @Test
  fun testDropdownChoices() {
    var currentValue = ""
    composeTestRule.setContent {
      WritableDropdown(
          label = "Select Item",
          placeholder = "Choose an option",
          value = currentValue,
          onValueChange = { currentValue = it },
          choices = listOf("Apple", "Banana", "Cherry"))
    }

    composeTestRule.onNodeWithTag("dropdown").performTextInput("A")
    composeTestRule.onNodeWithTag("item_Apple").assertIsDisplayed()
  }
}
