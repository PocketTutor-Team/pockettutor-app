package com.github.se.project.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.se.project.utils.countries
import org.junit.Rule
import org.junit.Test

class PhoneNumberInputTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testPhoneNumberInputRenders() {
    val selectedCountry = countries[0]
    val phoneNumber = ""
    composeTestRule.setContent {
      PhoneNumberInput(
          selectedCountry = selectedCountry,
          onCountryChange = {},
          phoneNumber = phoneNumber,
          onPhoneNumberChange = {})
    }

    // Check that the Country Code field is displayed
    composeTestRule.onNodeWithTag("countryCodeField").assertIsDisplayed()

    // Check that the Phone Number field is displayed
    composeTestRule.onNodeWithTag("phoneNumberField").assertIsDisplayed()
  }

  @Test
  fun testPhoneNumberInputUpdatesValue() {
    var currentPhoneNumber = ""
    composeTestRule.setContent {
      PhoneNumberInput(
          selectedCountry = countries[0],
          onCountryChange = {},
          phoneNumber = currentPhoneNumber,
          onPhoneNumberChange = { currentPhoneNumber = it })
    }

    composeTestRule.waitForIdle()

    // Enter a phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")

    // Check that the phone number is updated
    assert(currentPhoneNumber == "1234567890")
  }

  @Test
  fun testCountryCodeDropdown() {
    var currentSelectedCountry = countries[0]
    composeTestRule.setContent {
      PhoneNumberInput(
          selectedCountry = currentSelectedCountry,
          onCountryChange = { currentSelectedCountry = it },
          phoneNumber = "",
          onPhoneNumberChange = {})
    }

    composeTestRule.waitForIdle()
    // Open the country code dropdown
    composeTestRule.onNodeWithTag("countryCodeField").performClick()

    // Wait for the dropdown to appear
    composeTestRule.waitForIdle()

    // Select the second country in the list
    val secondCountry = countries[1]
    val testTag = "country_${secondCountry.code.replace("+", "plus")}"

    composeTestRule.onNodeWithTag(testTag).performClick()

    // Check that the selected country has been updated
    assert(currentSelectedCountry == secondCountry)
  }
}
