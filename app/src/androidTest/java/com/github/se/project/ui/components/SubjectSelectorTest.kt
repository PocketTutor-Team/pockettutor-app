package com.github.se.project.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.Subject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SubjectSelectorTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun selectingSubjectsUpdatesCorrectly() {
    val subjects = mutableStateOf<Subject>(Subject.NONE)
    composeTestRule.setContent { SubjectSelector(subjects) }

    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdownItem-${subject.name}").isNotDisplayed()
    }
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdownItem-${subject.name}").assertIsDisplayed()
    }
  }
}
