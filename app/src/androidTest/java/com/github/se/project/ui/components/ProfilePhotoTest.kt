package com.github.se.project.ui.components

import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ProfilePhotoTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockUri: Uri = mock(Uri::class.java)

  @Test
  fun profilePhotoDisplaysCorrectly_WithUri() {
    // When
    composeTestRule.setContent { ProfilePhoto(photoUri = mockUri) }

    // Then
    composeTestRule
        .onNodeWithTag("profilePhoto")
        .assertExists()
        .assertIsDisplayed()
        .assertHasNoClickAction()
  }

  @Test
  fun profilePhotoDisplaysCorrectly_WithoutUri() {
    // When
    composeTestRule.setContent { ProfilePhoto(photoUri = null) }

    // Then
    composeTestRule.onNodeWithTag("profilePhoto").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoDisplaysCorrectly_CustomSize() {
    // When
    composeTestRule.setContent { ProfilePhoto(photoUri = null, size = 120.dp) }

    // Then
    composeTestRule.onNodeWithTag("profilePhoto").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoDisplaysCorrectly_WithoutPlaceholder() {
    // When
    composeTestRule.setContent { ProfilePhoto(photoUri = null, showPlaceholder = false) }

    // Then
    composeTestRule.onNodeWithTag("profilePhoto").assertExists().assertIsDisplayed()
  }
}
