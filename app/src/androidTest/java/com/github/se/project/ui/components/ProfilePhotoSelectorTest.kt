package com.github.se.project.ui.components

import android.Manifest
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ProfilePhotoSelectorTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  private val mockUri: Uri = mock(Uri::class.java)
  private val mockOnPhotoSelected: (Uri?) -> Unit = mock()

  @Test
  fun profilePhotoSelector_InitialStateDisplaysCorrectly() {
    // When
    composeTestRule.setContent {
      ProfilePhotoSelector(currentPhotoUrl = null, onLocalPhotoSelected = mockOnPhotoSelected)
    }

    // Then
    composeTestRule.onNodeWithContentDescription("Add Photo").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoSelector_DisplaysCurrentPhoto() {
    // When
    composeTestRule.setContent {
      ProfilePhotoSelector(currentPhotoUrl = mockUri, onLocalPhotoSelected = mockOnPhotoSelected)
    }

    // Then
    composeTestRule.onNodeWithContentDescription("Profile Photo").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoSelector_ClickOpensBottomSheet() {
    // When
    composeTestRule.setContent {
      ProfilePhotoSelector(currentPhotoUrl = null, onLocalPhotoSelected = mockOnPhotoSelected)
    }

    // Then
    composeTestRule.onNodeWithContentDescription("Add Photo").performClick()

    composeTestRule.onNodeWithText("Take Photo").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("Choose from Gallery").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoSelector_DeleteOptionAppearsWithPhoto() {
    // When
    composeTestRule.setContent {
      ProfilePhotoSelector(currentPhotoUrl = mockUri, onLocalPhotoSelected = mockOnPhotoSelected)
    }

    // Click to open bottom sheet
    composeTestRule.onNodeWithContentDescription("Profile Photo").performClick()

    // Then
    composeTestRule.onNodeWithText("Remove Photo").assertExists().assertIsDisplayed()
  }

  @Test
  fun profilePhotoSelector_DeletePhotoCallsCallback() {
    // When
    composeTestRule.setContent {
      ProfilePhotoSelector(currentPhotoUrl = mockUri, onLocalPhotoSelected = mockOnPhotoSelected)
    }

    // Open bottom sheet and click delete
    composeTestRule.onNodeWithContentDescription("Profile Photo").performClick()
    composeTestRule.onNodeWithText("Remove Photo").performClick()

    // Then
    verify(mockOnPhotoSelected).invoke(null)
  }
}
