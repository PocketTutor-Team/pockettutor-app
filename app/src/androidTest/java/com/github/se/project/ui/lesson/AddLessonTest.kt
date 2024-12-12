package com.github.se.project.ui.lesson

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.R
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.validateLessonInput
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AddLessonTest {

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @get:Rule val composeTestRule = createComposeRule()

  // Mock NetworkStatusViewModel to control the network status state
  private val mockIsConnected = MutableStateFlow(true)
  private lateinit var networkStatusViewModel: NetworkStatusViewModel

  private val navigationActions = mock(NavigationActions::class.java)
  private val profile =
      Profile(
          uid = "uid",
          token = "",
          googleUid = "googleUid",
          firstName = "firstName",
          lastName = "lastName",
          phoneNumber = "phoneNumber",
          role = Role.TUTOR,
          section = Section.AR,
          academicLevel = AcademicLevel.BA1,
          description = "",
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 0)
  private val mockProfiles =
      mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(profile))
        `when`(selectedProfile).thenReturn(MutableStateFlow<Profile?>(null))
      }
  private val mockLessonRepository = mock(LessonRepository::class.java)

  private val mockLessons = LessonViewModel(mockLessonRepository)

  private val context = ApplicationProvider.getApplicationContext<Context>()
  // Accessing from resources
  private val okMessage = context.getString(R.string.ok)

  @Before
  fun setUp() {
    networkStatusViewModel =
        object : NetworkStatusViewModel(application = ApplicationProvider.getApplicationContext()) {
          override val isConnected = mockIsConnected
        }

    whenever(mockLessonRepository.getNewUid()).thenReturn("mockUid")
    whenever(
            mockLessonRepository.addLesson(
                org.mockito.kotlin.any(), org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { invocation ->
          @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess() // Simulate a successful update
        }
  }

  @Test
  fun addLessonIsProperlyDisplayed() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel)
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
            1.0,
            context) == null)
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "",
            1.0,
            1.0,
            context) == "time is missing")
  }

  @Test
  fun confirmWithEmptyFieldsShowsToast() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel)
    }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  @Test
  fun confirmWithValidFieldsNavigatesToHome() {
    var testMapReady by mutableStateOf(false)

    composeTestRule.setContent {
      AddLessonScreen(
          navigationActions,
          mockProfiles,
          mockLessons,
          networkStatusViewModel,
          onMapReadyChange = { testMapReady = it })
    }

    // Fill in the required fields
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    // Select Date and Time
    composeTestRule.onNodeWithTag("DateButton").performClick()
    composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText(okMessage).isDisplayed() }
    composeTestRule.onNodeWithText(okMessage).performClick()
    composeTestRule.onNodeWithTag("TimeButton").performClick()
    composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText(okMessage).isDisplayed() }
    composeTestRule.onNodeWithText(okMessage).performClick()

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
    composeTestRule.waitUntil(20000) {
      // wait max 15 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      testMapReady
    }
    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("googleMap").isDisplayed() }

    // click in the middle of GoogleMap
    composeTestRule.onNodeWithTag("googleMap").performTouchInput { click(center) }

    composeTestRule.waitUntil(20000) {
      assertEnabledToBoolean(composeTestRule.onNodeWithTag("confirmLocation"))
    }

    composeTestRule.onNodeWithTag("confirmLocation").performClick()

    // Confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun goBack() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel)
    }
    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel, {})
    }
    composeTestRule.onNodeWithText("Select Date").assertExists()
    composeTestRule.onNodeWithText("Select Time").assertExists()
  }

  @Test
  fun testInstantLessonDisplaying() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel, {})
    }

    // Set Instant Lesson
    composeTestRule.waitUntil(15000) {
      composeTestRule.onNodeWithTag("instantButton").isDisplayed()
    }
    composeTestRule.onNodeWithTag("instantButton").performClick()

    // Check that the map, Date, and Time buttons are not displayed
    composeTestRule.onNodeWithTag("mapButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("DateButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("TimeButton").assertIsNotDisplayed()

    // Test that the UI is correctly displayed
    composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DescriptionField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("subjectButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceRangeSlider").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }

  @Test
  fun testInstantLesson() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel)
    }

    // Set Instant Lesson
    composeTestRule.waitUntil(20000) {
      composeTestRule.onNodeWithTag("instantButton").isDisplayed()
    }

    // Set Title and Description
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("instantButton").performClick()
    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("mapButton").isNotDisplayed() }

    // Set Subject and Language
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performClick()
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC}").performClick()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(20000) {
      composeTestRule.onNodeWithText(context.getString(R.string.select_subject)).isNotDisplayed()
    }

    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testInstantInvalid() {
    composeTestRule.setContent {
      AddLessonScreen(navigationActions, mockProfiles, mockLessons, networkStatusViewModel, {})
    }

    // Set Instant Lesson
    composeTestRule.waitUntil(15000) {
      composeTestRule.onNodeWithTag("instantButton").isDisplayed()
    }
    composeTestRule.onNodeWithTag("instantButton").performClick()

    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }
}
