package com.github.se.project.ui.lesson

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.validateLessonInput
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AddLessonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val navigationActions = mock(NavigationActions::class.java)
  private val profile =
      Profile(
          "uid",
          "googleUid",
          "firstName",
          "lastName",
          "phoneNumber",
          Role.TUTOR,
          Section.AR,
          AcademicLevel.BA1,
          listOf(Language.ENGLISH),
          listOf(Subject.ANALYSIS),
          List(7) { List(12) { 0 } },
          0)
  private val mockProfiles =
      mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(profile))
      }
    private val mockLessonRepository = mock(LessonRepository::class.java)

    private val mockLessons = LessonViewModel(mockLessonRepository)

    @Before
    fun setUp() {
        whenever(mockLessonRepository.getNewUid()).thenReturn("mockUid")
        whenever(mockLessonRepository.addLesson(org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenAnswer { invocation ->
            val onSuccess = invocation.arguments[1] as () -> Unit
            onSuccess() // Simulate a successful update
        }
    }

  @Test
  fun AddLessonIsProperlyDisplayed() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }
    composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
  }

  @Test
  fun sliderTest() {
    var changed = false
    composeTestRule.setContent { PriceRangeSlider("testLabel", { _, _ -> changed = true }) }
    composeTestRule.onNodeWithTag("priceRangeSlider").performTouchInput { swipeRight() }
    assert(changed)
  }

  @Test
  fun validateValidatesValidly() {
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "time",
            1.0,
            1.0) == null)
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "",
            1.0,
            1.0) == "time is missing")
  }

  @Test
  fun confirmWithEmptyFieldsShowsToast() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  @Test
  fun confirmWithValidFieldsNavigatesToHome() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }

    // Fill in the required fields
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    // Select Date and Time
      composeTestRule.onNodeWithTag("DateButton").performClick()
      onView(withText("OK")).perform(click())
      composeTestRule.onNodeWithTag("TimeButton").performClick()
      onView(withText("OK")).perform(click())

    // Set Subject and Language
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC}").performClick()
      composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()

      // Select location
      composeTestRule.onNodeWithTag("mapButton").performClick()
      composeTestRule.onNodeWithTag("map").performClick()
      Thread.sleep(2000) // Wait for the map to load
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
      device.click(device.displayWidth / 2, device.displayHeight / 2)
      composeTestRule.onNodeWithTag("confirmLocation").performClick()

    // Confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

    @Test
    fun goBack(){
        composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }
        composeTestRule.onNodeWithTag("backButton").performClick()
        verify(navigationActions).navigateTo(anyString())
    }

  @Test
  fun testInitialState() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }
    composeTestRule.onNodeWithText("Select Date").assertExists()
    composeTestRule.onNodeWithText("Select Time").assertExists()
  }
}
