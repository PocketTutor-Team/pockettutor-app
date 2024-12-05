package com.github.se.project.ui.components

/*@RunWith(AndroidJUnit4::class)
class SingleSubjectSelectorTest {

  private var settedUp = false

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val selectedSubject = mutableStateOf(Subject.NONE)
    Thread.sleep(10000)
    runOnUiThread {
      composeTestRule.setContent { SubjectSelector(selectedSubject = selectedSubject) }
    }
    composeTestRule.waitForIdle()
    settedUp = true
  }

  @Test
  fun selectingSubjectUpdatesCorrectly() {
    assert(settedUp)
    composeTestRule.waitforIdle()

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
}*/
