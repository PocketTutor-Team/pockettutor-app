package com.github.se.project.ui.lesson

// import com.github.se.project.ui.map.MapPickerBox

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

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
          "",
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
    whenever(
            mockLessonRepository.addLesson(
                org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess() // Simulate a successful update
        }
  }

  @Test
  fun AddLessonIsProperlyDisplayed() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, onMapReadyChange = {})
    }
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
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, onMapReadyChange = {})
    }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  /*
  @Test
  fun confirmWithValidFieldsNavigatesToHome() {
    var testMapReady by mutableStateOf(false)

    composeTestRule.setContent {
      AddLessonScreen(
          navigationActions, mockProfiles, mockLessons, onMapReadyChange = { testMapReady = it })
    }

    // Fill in the required fields
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    // Select Date and Time
    composeTestRule.onNodeWithTag("DateButton").performClick()
    Thread.sleep(2000)
    // composeTestRule.onNodeWithText("OK").performClick()
    onView(withText("OK")).perform(click())
    composeTestRule.onNodeWithTag("TimeButton").performClick()
    Thread.sleep(2000)
    // composeTestRule.onNodeWithText("OK").performClick()
    onView(withText("OK")).perform(click())

    // Set Subject and Language
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC}").performClick()
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()

    // Select location
    composeTestRule.onNodeWithTag("mapButton").performClick()
    composeTestRule.onNodeWithTag("mapContainer").performClick()

    // replace the following code with the composeTestRule equivalent as
    // the Thread.sleep() method is not recommended and
    // device.click() is not well supported in compose
    composeTestRule.waitUntil(15000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      testMapReady
    }

    // click in the middle of GoogleMap
    composeTestRule.onNodeWithTag("googleMap").performTouchInput { click(center) }

    composeTestRule.onNodeWithTag("confirmLocation").performClick()

    // Confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }*/

  @Test
  fun goBack() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, onMapReadyChange = {})
    }
    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, onMapReadyChange = {})
    }
    composeTestRule.onNodeWithText("Select Date").assertExists()
    composeTestRule.onNodeWithText("Select Time").assertExists()
  }
}
