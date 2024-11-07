package com.github.se.project.ui

import android.content.Context
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

  @Mock lateinit var navigationActions: NavigationActions

  @Mock lateinit var context: Context

  // Mock du ProfilesRepository
  private val mockProfileRepository = Mockito.mock(ProfilesRepository::class.java)

  private val mockProfileViewModel = ListProfilesViewModel(mockProfileRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)

  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  var currentLesson: Lesson? = null

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getNewUid()).thenReturn("mockUid")
    whenever(mockLessonRepository.getNewUid()).thenReturn("mockUid")
    whenever(mockLessonRepository.addLesson(any(), any(), any())).thenAnswer { invocation ->
      currentLesson = invocation.arguments[0] as Lesson
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    /*whenever(mockLessonRepository.getLessonsForStudent(any(), any(), any())).thenAnswer { invocation
      ->
      val onComplete = invocation.arguments[1] as (List<Lesson>) -> Unit
      if (currentLesson != null) {
        onComplete(listOf(currentLesson!!)) // Simulate a successful update
      }
    }*/
  }

  // End to end test, for the whole app, firebase included
  @Test
  fun EndToEnd() {
    // Start the app in test mode
    composeTestRule.setContent {
      PocketTutorApp(true, viewModel(), mockProfileViewModel, mockLessonViewModel)
    }

    // Sign in
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // Enter valid data for all fields
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Alice")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Dupont")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

    // Select section and academic level
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Go to the profile viewing screen
    composeTestRule.onNodeWithTag("Profile Icon", true).performClick()

    // Check if the correct profile info is displayed
    composeTestRule.onNodeWithText("Alice Dupont").assertIsDisplayed()
    composeTestRule.onNodeWithText("Status: BA3 Student").assertIsDisplayed()
    composeTestRule.onNodeWithText("Section: SC").assertIsDisplayed()

    // Go to the edit profile screen
    composeTestRule.onNodeWithTag("editProfileButton").performClick()

    // Change the profile info
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA5").performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-MA").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Check if the profile info updated correctly
    composeTestRule.onNodeWithText("Status: BA5 Student").assertIsDisplayed()
    composeTestRule.onNodeWithText("Section: MA").assertIsDisplayed()

    // Go back to the home screen
    composeTestRule.onNodeWithTag("closeButton").performClick()

    composeTestRule.onNodeWithText("Find a Tutor").performClick()

    composeTestRule.onNodeWithTag("titleField").performTextInput("End-to-end testing")
    composeTestRule
        .onNodeWithTag("DescriptionField")
        .performTextInput("Teach me how to write tests :(")
    composeTestRule.onNodeWithTag("DateButton").performClick()
    onView(withText("OK")).perform(click())
    composeTestRule.onNodeWithTag("TimeButton").performClick()
    onView(withText("OK")).perform(click())
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdownAICC").performClick()
    composeTestRule.onNodeWithTag("mapButton").performClick()
    composeTestRule.onNodeWithTag("map").performClick()
    Thread.sleep(2000)
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.click(device.displayWidth / 2, device.displayHeight / 2)
    composeTestRule.onNodeWithTag("confirmLocation").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    Log.e("abagaga", "profile: ${currentLesson.toString()}")
    /*assert(currentLesson != null)
    assert(currentLesson!!.title == "End-to-end testing")
    assert(currentLesson!!.description == "Teach me how to write tests :(")
    assert(currentLesson!!.subject == Subject.AICC)
    assert(currentLesson!!.languages.contains(Language.ENGLISH))*/
  }
}
