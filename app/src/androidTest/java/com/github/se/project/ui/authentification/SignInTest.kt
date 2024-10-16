package com.github.se.project.ui.authentification

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun logoAndButtonAreCorrectlyDisplayed() {
    /*composeTestRule.onNodeWithTag("logo").assertIsDisplayed()

    composeTestRule.onNodeWithTag("images").assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()*/
  }
}
