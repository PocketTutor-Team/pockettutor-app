package com.github.se.project.ui.lesson


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.Screen


@RunWith(AndroidJUnit4::class)
class EditRequestedLessonScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val navigationActions = mock(NavigationActions::class.java)
    private val profile = Profile(
        uid = "uid",
        googleUid = "googleUid",
        firstName = "firstName",
        lastName = "lastName",
        phoneNumber = "phoneNumber",
        role = Role.STUDENT,
        section = Section.AR,
        AcademicLevel.BA1,
        listOf(Language.ENGLISH),
        listOf(Subject.ANALYSIS),
        List(7) { List(12) { 0 } },
        0
    )

    private val mockProfiles = mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow(profile))
    }

    private val mockLessons = mock(LessonViewModel::class.java)

    @Test
    fun editRequestedLessonDisplaysCorrectLessonInfo() {
        val lesson = Lesson(
            id = "lessonId1",
            title = "Sample Lesson",
            description = "This is a sample lesson.",
            subject = Subject.ANALYSIS,
            languages = listOf(Language.ENGLISH),
            minPrice = 20.0,
            maxPrice = 40.0,
            price = 30.0,
            status = LessonStatus.PENDING,
            studentUid = profile.uid
        )
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(listOf(lesson)))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "lessonId1",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("titleField").assertTextContains("Sample Lesson")
        composeTestRule.onNodeWithTag("descriptionField").assertTextContains("This is a sample lesson.")
        composeTestRule.onNodeWithTag("subjectField").assertTextContains("ANALYSIS")
        composeTestRule.onNodeWithTag("languagesField").assertTextContains("ENGLISH")
        composeTestRule.onNodeWithTag("priceField").assertTextContains("30.0")
    }

    @Test
    fun confirmLessonUpdatesNavigatesToHome() {
        val lesson = Lesson(id = "lessonId1", title = "Original Title", description = "Original Description")
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(listOf(lesson)))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "lessonId1",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("titleField").performTextInput("Updated Title")
        composeTestRule.onNodeWithTag("descriptionField").performTextInput("Updated Description")
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        verify(mockLessons).updateLesson(any(Lesson::class.java), any())
        verify(navigationActions).navigateTo(Screen.HOME)
    }

    @Test
    fun deleteLessonNavigatesToHome() {
        val lesson = Lesson(id = "lessonId1", title = "To be deleted")
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(listOf(lesson)))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "lessonId1",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("deleteButton").performClick()
        verify(mockLessons).deleteLesson("lessonId1", any())
        verify(navigationActions).navigateTo(Screen.HOME)
    }

    @Test
    fun lessonNotFoundReturnsEarly() {
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(emptyList()))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "invalidLessonId",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("lessonContent").assertDoesNotExist()
    }

    @Test
    fun confirmWithEmptyFieldsShowsToast() {
        val lesson = Lesson(id = "lessonId1", title = "", description = "")
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(listOf(lesson)))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "lessonId1",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("confirmButton").performClick()
        verify(navigationActions, never()).navigateTo(Screen.HOME)
    }

    @Test
    fun testInitialStateShowsDefaultDateAndTime() {
        val lesson = Lesson(
            id = "lessonId1",
            title = "Title",
            description = "Description",
            timeSlot = "11/04/2024T10:00:00",
            status = LessonStatus.PENDING
        )
        `when`(mockLessons.currentUserLessons).thenReturn(MutableStateFlow(listOf(lesson)))

        composeTestRule.setContent {
            EditRequestedLessonScreen(
                lessonId = "lessonId1",
                navigationActions = navigationActions,
                listProfilesViewModel = mockProfiles,
                lessonViewModel = mockLessons
            )
        }

        composeTestRule.onNodeWithTag("DateButton").assertTextContains("04/11/2024")
        composeTestRule.onNodeWithTag("TimeButton").assertTextContains("10:00")
    }
}
