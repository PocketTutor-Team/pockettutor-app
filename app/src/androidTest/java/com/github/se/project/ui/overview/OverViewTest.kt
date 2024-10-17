package com.github.se.project.ui.overview


import HomeScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import java.util.EnumSet
/*
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var listProfilesViewModel: ListProfilesViewModel
    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var navigationActions: NavigationActions

    private val profile = Profile(
        "uid",
        "googleUid",
        "firstName",
        "lastName",
        "phoneNumber",
        Role.TUTOR,
        Section.AR,
        AcademicLevel.BA1,
        EnumSet.of(Language.ENGLISH),
        EnumSet.of(Subject.ANALYSIS),
        List(7) { List(12) { 0 } },
        0
    )

    private val mockLessons = listOf(Lesson("Math"), Lesson("Science"))
    private val currentUserLessonsFlow = MutableStateFlow<List<Lesson>>(mockLessons)

    @Before
    fun setup() {
        // Mocking the ViewModels
        listProfilesViewModel = Mockito.mock(ListProfilesViewModel::class.java).apply {
            Mockito.`when`(currentProfile).thenReturn(MutableStateFlow(profile))
        }
        lessonViewModel = Mockito.spy(LessonViewModel(mock<LessonRepository>())).apply {
            when((this).currentUser).thenReturn((currentUserLessonsFlow))
        }
        navigationActions = Mockito.mock(NavigationActions::class.java).apply {
            // Ensure that currentRoute() returns a valid string
            Mockito.`when`(currentRoute()).thenReturn(Route.HOME) // or whatever the default route is
        }

        // Set content to test
        composeTestRule.setContent {
            HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
    }

    @Test
    fun testProfileIconClickable() {
        composeTestRule.onNodeWithContentDescription("Profile Icon")
            .performClick()
        verify(navigationActions).navigateTo(Mockito.anyString())
    }

    @Test
    fun testLessonItemsDisplayed() {
        composeTestRule.onNodeWithText("Math").assertIsDisplayed() // Corrected expected text
        composeTestRule.onNodeWithText("Science").assertIsDisplayed() // Corrected expected text
    }

    @Test
    fun testNoProfileFoundScreenDisplayed() {
        // Set a null profile in the ViewModel
        listProfilesViewModel = Mockito.mock(ListProfilesViewModel::class.java).apply {
            Mockito.`when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(null))
        }

        // Recompose
        composeTestRule.setContent {
            HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }

        // Verify that the "No profile is currently assigned" text is displayed
        composeTestRule.onNodeWithText("No profile is currently assigned to the current user.")
            .assertIsDisplayed()

        // Verify the button to go back to HOME screen is displayed
        composeTestRule.onNodeWithText("Go back to HOME screen").assertIsDisplayed()
    }

    @Test
    fun testLessonsEmptyMessageDisplayed() {
        // Set up empty lessons scenario
        currentUserLessonsFlow.value = emptyList() // Simulate no lessons
        composeTestRule.setContent {
            HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }

        // Verify the message indicating no lessons scheduled is displayed
        composeTestRule.onNodeWithText("You have no lessons scheduled at the moment.")
            .assertIsDisplayed()
    }
}*/