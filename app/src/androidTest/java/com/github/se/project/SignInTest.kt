package com.github.se.project

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun logoAndButtonAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("logo").assertIsDisplayed()

    composeTestRule.onNodeWithTag("images").assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun dotsAndTermsAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("dots").assertIsDisplayed()
    composeTestRule.onNodeWithTag("terms").assertIsDisplayed()
  }
}
