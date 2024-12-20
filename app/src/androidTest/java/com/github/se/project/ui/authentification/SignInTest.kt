package com.github.se.project.ui.authentification
/*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent { SignInScreen(onSignInClick = {}) }
  }

  /*@Test
  fun logoAndButtonAndImageScrollAreCorrectlyDisplayed() {
    // Assert logo is displayed
    composeTestRule.onNodeWithTag("logo").assertIsDisplayed()

    // Assert images pager is displayed
    composeTestRule.onNodeWithTag("images").assertIsDisplayed()

    // Assert button is displayed and has click action
    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed().assertHasClickAction()

    // Verify images scroll and display the correct content
    val pager = composeTestRule.onNodeWithTag("images")

    // Assert first image
    pager.performScrollToIndex(0)
    composeTestRule
        .onNodeWithText(
            "Welcome to Pocket Tutor, Simplify learning and teaching with instant connections to the university community.")
        .assertIsDisplayed()

    // Scroll to second image and verify text
    pager.performScrollToIndex(1)
    composeTestRule
        .onNodeWithText(
            "Get help or share your expertise – whether you're a student or a tutor, Pocket Tutor works for both.")
        .assertIsDisplayed()

    // Scroll to third image and verify text
    pager.performScrollToIndex(2)
    composeTestRule
        .onNodeWithText(
            "Pocket Tutor connects university students and tutors for quick, effective learning support.")
        .assertIsDisplayed()
  }*/

  @Test
  fun dotsAndTermsAreCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("dots").assertIsDisplayed()
    composeTestRule.onNodeWithTag("terms").assertIsDisplayed()
  }
}
*/
