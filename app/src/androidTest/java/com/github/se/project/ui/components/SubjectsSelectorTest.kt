package com.github.se.project.ui.components

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.project.model.profile.Subject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SubjectsSelectorTest {

  @get:Rule val composeTestRule = createComposeRule()
  private val selectedSubjects = mutableStateListOf<Subject>()

  @Before
  fun setUp() {
    // Set the content for the test
    composeTestRule.setContent {
      SubjectSelector(selectedSubjects = selectedSubjects, multipleSelection = true)
    }
  }

  @Test
  fun testSubjectsSelector_onClickDisplaysDropdownAndSelectsItem() {

    // Step 1: Check that the dropdown is not displayed initially
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC.name}").assertDoesNotExist()

    // Step 2: Click on the button to show the dropdown
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    // Step 3: Check if the dropdown is displayed
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC.name}").assertIsDisplayed()

    // Step 4: Click on an item in the dropdown to select it
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC.name}").performClick()

    // Step 5: Verify that the subject was added to selectedSubjects
    assertTrue("AICC should be selected", selectedSubjects.contains(Subject.AICC))

    // Step 6: Click again to deselect the item
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC.name}").performClick()

    // Step 7: Verify that the subject was removed from selectedSubjects
    assertFalse("AICC should be deselected", selectedSubjects.contains(Subject.AICC))
  }
}
