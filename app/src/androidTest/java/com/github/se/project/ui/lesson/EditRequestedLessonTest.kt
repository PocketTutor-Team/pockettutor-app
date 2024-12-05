package com.github.se.project.ui.lesson

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.PriceRangeSlider
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
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EditRequestedLessonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private val context = ApplicationProvider.getApplicationContext<Context>()

  private val navigationActions = mock(NavigationActions::class.java)
  private val profile =
      Profile(
          "uid",
          "",
          "googleUid",
          "firstName",
          "lastName",
          "phoneNumber",
          Role.TUTOR,
          Section.AR,
          AcademicLevel.BA1,
          "I have experience teaching math and physics.",
          listOf(Language.ENGLISH),
          listOf(Subject.ANALYSIS),
          List(7) { List(12) { 0 } },
          0)
  private val lessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = mutableListOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "10/10/2024T10:00:00",
              status = LessonStatus.CONFIRMED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = mutableListOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "10/10/2024T11:00:00",
              status = LessonStatus.CONFIRMED,
              latitude = 0.0,
              longitude = 0.0))
  private val mockProfiles =
      mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(profile))
      }

  private val mockLessonRepository = mock(LessonRepository::class.java)
  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  // Mock NetworkStatusViewModel to control the network status state
  private val mockIsConnected = MutableStateFlow(true)
  private lateinit var networkStatusViewModel: NetworkStatusViewModel

  @Before
  fun setup() {

    networkStatusViewModel =
        object :
            NetworkStatusViewModel(
                application = androidx.test.core.app.ApplicationProvider.getApplicationContext()) {
          override val isConnected = mockIsConnected
        }

    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockLessonRepository.getLessonsForStudent(any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
      onSuccess(lessons)
    }
    whenever(mockLessonRepository.deleteLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful deletion
    }
    mockLessonViewModel.selectLesson(lessons[0])
  }

  @Test
  fun EditRequestedLessonIsProperlyDisplayed() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(
          navigationActions,
          mockProfiles,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = {})
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
  }

  @Test
  fun sliderTest() {
    var changed = false
    composeTestRule.setContent { PriceRangeSlider("testLabel", { a, b -> changed = true }) }
    composeTestRule.onNodeWithTag("priceRangeSlider").performTouchInput { swipeRight() }
    assert(changed)
  }

  @Test
  fun confirmWithEmptyFieldsShowsToast() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(
          navigationActions,
          mockProfiles,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = {})
    }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }
  /*
    @Test
    fun confirmWithValidFieldsNavigatesToHome() {
      var testMapReady by mutableStateOf(false)
      composeTestRule.setContent {
        EditRequestedLessonScreen(
            navigationActions,
            mockProfiles,
            mockLessonViewModel,
            onMapReadyChange = { testMapReady = it })
      }

      // Fill in the required fields
      composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
      composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

      // Select Date and Time
      composeTestRule.onNodeWithTag("DateButton").performClick()
      composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText("OK").isDisplayed() }
      composeTestRule.onNodeWithText("OK").performClick()
      composeTestRule.onNodeWithTag("TimeButton").performClick()
      composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText("OK").isDisplayed() }
      composeTestRule.onNodeWithText("OK").performClick()

      // Set Subject and Language
      composeTestRule.onNodeWithTag("subjectButton").performClick()
      composeTestRule.onNodeWithTag("dropdown${Subject.ANALYSIS}").performClick()
      composeTestRule.onNodeWithTag("languageSelectorRow").performClick()

      // Select location
      composeTestRule.onNodeWithTag("mapButton").performClick()
      composeTestRule.onNodeWithTag("mapContainer").performClick()

      // replace the following code with the composeTestRule equivalent as
      // the Thread.sleep() method is not recommended and
      // device.click() is not well supported in compose
      composeTestRule.waitUntil(15000) {
        // wait max 4 seconds for the map to load,
        // as soon as the map is ready, the next line will be executed
        testMapReady
      }

      composeTestRule.onNodeWithTag("googleMap").performTouchInput { click(center) }

      composeTestRule.onNodeWithTag("confirmLocation").performClick()

      // Confirm
      composeTestRule.onNodeWithTag("confirmButton").performClick()
      verify(navigationActions).navigateTo(anyString())
    }
  */
  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(
          navigationActions,
          mockProfiles,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = {})
    }
    composeTestRule.onNodeWithText("10/10/2024").assertExists()
    composeTestRule.onNodeWithText("10:00").assertExists()
  }
}
