package com.github.se.project.ui.message

/*import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.models.Filters.eq
import io.getstream.chat.android.models.InitializationState
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ChannelScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock dependencies
  private val mockNavigationActions: NavigationActions = mock(NavigationActions::class.java)
  private val mockListProfilesViewModel: ListProfilesViewModel =
      mock(ListProfilesViewModel::class.java)
  private val mockLessonViewModel: LessonViewModel = mock(LessonViewModel::class.java)
  private val mockChatViewModel: ChatViewModel = mock(ChatViewModel::class.java)
  private val mockNetworkStatusViewModel: NetworkStatusViewModel =
      mock(NetworkStatusViewModel::class.java)

  private val testProfile =
      Profile(
          uid = "abc",
          token = "abc",
          googleUid = "abc",
          firstName = "abc",
          lastName = "abc",
          phoneNumber = "12345",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA5,
          schedule = listOf(listOf(0)),
          price = 0)

  private val currentProfile =
      Profile(
          uid = "KamHffgIcnsmQh2WO2Rs",
          token =
              "cLt4et0KQI6agrN3uVPNSL:APA91bGHyyk_hKdxto9EC021rY8NttRYUHAnlkGvcsjfke7qjhfDOQ0ccFQYI_ynyTwVY8SrfuZK7nPXLX4gnJzRfKjB-Y-MOIxhLzOwKgWIULErtI22e7E",
          googleUid = "znWuBh6dZGQmpenSPqA5w2zeBNp2",
          firstName = "Philipp",
          lastName = "Glaser",
          phoneNumber = "12345",
          role = Role.STUDENT,
          section = Section.IN,
          academicLevel = AcademicLevel.BA5,
          schedule = listOf(listOf(0)),
          price = 0)

    val mockInitializationState = MutableStateFlow(InitializationState.COMPLETE)
    val mockCurrentProfile = MutableStateFlow(testProfile)
    val mockLessons = MutableStateFlow(emptyList<Lesson>())

  @Before
  fun setUp() {
      MockitoAnnotations.openMocks(this)
    // Mock MutableStateFlow with mockk
    whenever(mockListProfilesViewModel.currentProfile).thenReturn(mockCurrentProfile)

    // Mock the StateFlow of clientInitializationState
      doReturn(mockInitializationState).`when`(mockChatViewModel).clientInitializationState

    // Mock currentUserLessons StateFlow
      doReturn(mockLessons).`when`(mockLessonViewModel).currentUserLessons

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }
  }

  @Test
  fun testInitializationComplete() {
    // Assert: Check if the ChannelsScreen is displayed after initialization is complete
    composeTestRule.onNodeWithText("PocketTutor Chat").assertIsDisplayed()
  }

  @Test
  fun testNavigationOnChannelClick() {
    // Arrange: Set up a mock channel click action
    val testChannelId = "messaging:G3KKMrwYqwlrNIl3f9Li_SJp0mYlngBqMWNC3kHsC"

    every { mockChatViewModel.setCurrentChannelId(testChannelId) } just Runs

    // Act: Simulate channel click
    composeTestRule.onNodeWithText("PocketTutor Chat").performClick()

    // Assert: Verify that the channel ID was set and navigation occurred
    verify(mockChatViewModel).setCurrentChannelId(eq(testChannelId))
    verify(mockNavigationActions).navigateTo(Screen.CHAT)
  }

  @Test
  fun testBottomNavigationMenu() {
    // Act: Click the "Home" tab in the bottom navigation
    composeTestRule.onNodeWithText("Home").performClick()

    // Assert: Verify navigation to the Home screen
    verify(mockNavigationActions).navigateTo(Screen.HOME)
  }
}
*/