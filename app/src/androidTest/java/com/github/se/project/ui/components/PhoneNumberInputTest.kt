package com.github.se.project.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.github.se.project.utils.countries
import org.junit.Rule
import org.junit.Test

class PhoneNumberInputTest {

  @get:Rule val composeTestRule = createEmptyComposeRule()

  @Test
  fun testPhoneNumberInputRenders() {
    // Mutable states to hold the values
    val selectedCountry = mutableStateOf(countries[0])
    val phoneNumber = mutableStateOf("")

    ActivityScenario.launch<ComponentActivity>(
            Intent(ApplicationProvider.getApplicationContext(), ComponentActivity::class.java))
        .use { scenario ->
          scenario.onActivity { activity ->
            activity.setContent {
              MaterialTheme {
                PhoneNumberInput(
                    selectedCountry = selectedCountry.value,
                    onCountryChange = { selectedCountry.value = it },
                    phoneNumber = phoneNumber.value,
                    onPhoneNumberChange = { phoneNumber.value = it })
              }
            }
          }

          // Interact with the UI
          composeTestRule.onNodeWithTag("countryCodeField").assertIsDisplayed()
          composeTestRule.onNodeWithTag("phoneNumberField").assertIsDisplayed()
        }
  }

  @Test
  fun testPhoneNumberInputUpdatesValue() {
    val selectedCountry = mutableStateOf(countries[0])
    val phoneNumber = mutableStateOf("")

    ActivityScenario.launch<ComponentActivity>(
            Intent(ApplicationProvider.getApplicationContext(), ComponentActivity::class.java))
        .use { scenario ->
          scenario.onActivity { activity ->
            activity.setContent {
              MaterialTheme {
                PhoneNumberInput(
                    selectedCountry = selectedCountry.value,
                    onCountryChange = { selectedCountry.value = it },
                    phoneNumber = phoneNumber.value,
                    onPhoneNumberChange = { phoneNumber.value = it })
              }
            }
          }

          composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")

          // Verify the updated phone number
          scenario.onActivity { assert(phoneNumber.value == "1234567890") }
        }
  }

  @Test
  fun testCountryCodeDropdown() {
    val selectedCountry = mutableStateOf(countries[0])
    val phoneNumber = mutableStateOf("")

    ActivityScenario.launch<ComponentActivity>(
            Intent(ApplicationProvider.getApplicationContext(), ComponentActivity::class.java))
        .use { scenario ->
          scenario.onActivity { activity ->
            activity.setContent {
              MaterialTheme {
                PhoneNumberInput(
                    selectedCountry = selectedCountry.value,
                    onCountryChange = { selectedCountry.value = it },
                    phoneNumber = phoneNumber.value,
                    onPhoneNumberChange = { phoneNumber.value = it })
              }
            }
          }

          composeTestRule.waitForIdle()

          // Open the country code dropdown
          composeTestRule.onNodeWithTag("countryCodeField").performClick()

          composeTestRule.waitForIdle()

          // Select the second country
          val secondCountry = countries[1]
          val testTag = "country_${secondCountry.code.replace("+", "plus")}"

          composeTestRule.onNodeWithTag(testTag).performClick()

          // Verify the selected country
          scenario.onActivity { assert(selectedCountry.value == secondCountry) }
        }
  }
}
