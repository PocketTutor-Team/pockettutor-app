package com.github.se.project.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.se.project.model.profile.Subject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingleSubjectSelectorTest {

  private var settedUp = false

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val selectedSubject = mutableStateOf<Subject>(Subject.NONE)
    Thread.sleep(10000)
    runOnUiThread {
      composeTestRule.setContent { SubjectSelector(selectedSubject = selectedSubject) }
    }
    composeTestRule.waitForIdle()
    settedUp = true
    Thread.sleep(10000)
  }

  @Test
  fun selectingSubjectUpdatesCorrectly() {
    assert(settedUp)
    Thread.sleep(10000)

    // Initially, dropdown items are not displayed
    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").assertDoesNotExist()
    }

    // Click on the subject button to open the dropdown
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    // **Wait for the UI to settle after the click**
    composeTestRule.waitForIdle()

    // Now, dropdown items should be displayed
    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").assertIsDisplayed()
    }
  }
}
