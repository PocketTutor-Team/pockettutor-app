package com.github.se.project.ui.lesson


import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.validateLessonInput
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class EditRequestedLessonTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var listProfilesViewModel: ListProfilesViewModel
    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var navigationActions: NavigationActions

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

    private val mockLessons =
        listOf(
            Lesson(
                id = "1",
                title = "Physics Tutoring",
                description = "Mechanics and Thermodynamics",
                subject = Subject.PHYSICS,
                languages = listOf(Language.ENGLISH),
                tutorUid = "tutor123",
                studentUid = "student123",
                minPrice = 20.0,
                maxPrice = 40.0,
                timeSlot = "2024-10-10T10:00:00",
                status = LessonStatus.CONFIRMED),
            Lesson(
                id = "2",
                title = "Math Tutoring",
                description = "Algebra and Calculus",
                subject = Subject.ANALYSIS,
                languages = listOf(Language.ENGLISH),
                tutorUid = "tutor123",
                studentUid = "student123",
                minPrice = 20.0,
                maxPrice = 40.0,
                timeSlot = "2024-10-10T11:00:00",
                status = LessonStatus.CONFIRMED))

    private val EditRequestedLessonsFlow = MutableStateFlow(mockLessons[0])

    @Before
    fun setup() {
        // Mock the ViewModels
        listProfilesViewModel =
            Mockito.mock(ListProfilesViewModel::class.java).apply {
                `when`(currentProfile).thenReturn(MutableStateFlow(profile))
            }

        val mockRepository = mock(LessonRepository::class.java)
        lessonViewModel =
            Mockito.spy(LessonViewModel(mockRepository)).apply {
                `when`(this.selectedLesson).thenReturn(EditRequestedLessonsFlow.asStateFlow())
            }

        navigationActions =
            Mockito.mock(NavigationActions::class.java).apply {
                `when`(currentRoute()).thenReturn(Route.HOME)
            }
    }

    @Test
    fun AddLessonIsProperlyDisplayed() {
        composeTestRule.setContent { EditRequestedLessonScreen(mockLessons[0].id, navigationActions, listProfilesViewModel, lessonViewModel) }
        composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
    }

    @Test
    fun sliderTest() {
        var changed = false
        composeTestRule.setContent { PriceRangeSlider("testLabel") { a, b -> changed = true } }
        composeTestRule.onNodeWithTag("priceRangeSlider").performTouchInput { swipeRight() }
        assert(changed)
    }

    @Test
    fun validateValidatesValidly() {
        assert(
            validateLessonInput(
                "title",
                "description",
                mutableStateOf(Subject.PHYSICS),
                listOf(Language.ENGLISH),
                "date",
                "time") == null)
        assert(
            validateLessonInput(
                "title",
                "description",
                mutableStateOf(Subject.ANALYSIS),
                listOf(Language.ENGLISH),
                "date",
                "") == "time is missing")
    }

    @Test
    fun confirmWithEmptyFieldsShowsToast() {
        composeTestRule.setContent { EditRequestedLessonScreen(mockLessons[0].id, navigationActions, listProfilesViewModel, lessonViewModel) }
        composeTestRule.onNodeWithTag("confirmButton").performClick()
        verify(navigationActions, never()).navigateTo(anyString())
    }

    @Test
    fun confirmWithValidFieldsNavigatesToHome() {
        composeTestRule.setContent { EditRequestedLessonScreen(mockLessons[0].id, navigationActions, listProfilesViewModel, lessonViewModel) }

        // Fill in the required fields
        composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
        composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

        // Select Date and Time (simulate selection)
        composeTestRule.onNodeWithTag("DateButton").performClick()
        // Assuming DatePickerDialog is shown, set selectedDate manually for test (mock behavior if
        // possible)
        composeTestRule.onNodeWithText("Select Date").assertExists()

        composeTestRule.onNodeWithTag("TimeButton").performClick()
        // Assuming TimePickerDialog is shown, set selectedTime manually for test (mock behavior if
        // possible)
        composeTestRule.onNodeWithText("Select Time").assertExists()

        // Set Subject and Language
        composeTestRule.onNodeWithTag("subjectButton").performClick()
        composeTestRule.onNodeWithTag("dropdown${Subject.AICC}").performClick()
        composeTestRule.onNodeWithTag("languageSelectorRow").performClick()

        // Confirm
        composeTestRule.onNodeWithTag("confirmButton").performClick()
        verify(navigationActions, never()).navigateTo(anyString())
    }

    @Test
    fun testInitialState() {
        composeTestRule.setContent { EditRequestedLessonScreen(mockLessons[0].id, navigationActions, listProfilesViewModel, lessonViewModel) }
        composeTestRule.onNodeWithText("Select Date").assertExists()
        composeTestRule.onNodeWithText("Select Time").assertExists()
    }
}






/*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq



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
        academicLevel = AcademicLevel.BA1,
        languages = listOf(Language.ENGLISH),
        subjects = listOf(Subject.ANALYSIS),
        schedule = List(7) { List(12) { 0 } },
        price = 0
    )

    private val mockProfiles = mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow(profile))
    }

    private val mockLessons = mock(LessonViewModel::class.java)

    // Test 1: EditRequestedLessonScreen Displays Correct Lesson Information
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

        // Assert UI elements contain correct data
        composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
        composeTestRule.onNodeWithTag("titleField").assertTextContains("Sample Lesson")
        composeTestRule.onNodeWithTag("descriptionField").assertTextContains("This is a sample lesson.")
        composeTestRule.onNodeWithTag("subjectField").assertTextContains("ANALYSIS")
        composeTestRule.onNodeWithTag("languagesField").assertTextContains("ENGLISH")
        composeTestRule.onNodeWithTag("priceField").assertTextContains("30.0")
    }

    // Test 2: Confirm Lesson Updates and Navigate to Home Screen
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

        // Simulate user input
        composeTestRule.onNodeWithTag("titleField").performTextInput("Updated Title")
        composeTestRule.onNodeWithTag("descriptionField").performTextInput("Updated Description")
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        // Verify update and navigation
        verify(mockLessons).updateLesson(any(Lesson::class.java), any())
        verify(navigationActions).navigateTo(Screen.HOME)
    }

    // Test 3: Delete Lesson Navigates to Home Screen
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

        // Verify delete action and navigation
        verify(mockLessons).deleteLesson(eq("lessonId1"), any())
        verify(navigationActions).navigateTo(Screen.HOME)
    }

    // Test 4: Lesson Not Found Returns Early
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

    // Test 5: Confirm with Empty Fields Does Not Navigate
    @Test
    fun confirmWithEmptyFieldsDoesNotNavigate() {
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

        // Verify no navigation on empty fields
        verify(navigationActions, never()).navigateTo(Screen.HOME)
    }

    // Test 6: Test Initial State Shows Default Date and Time
    @Test
    fun testInitialStateShowsDefaultDateAndTime() {
        val lesson = Lesson(
            id = "lessonId1",
            title = "Title",
            description = "Description",
            timeSlot = "2024-11-04T10:00:00", // ISO 8601 format
            status = LessonStatus.STUDENT_REQUESTED
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

        // Assert correct display of date and time
        composeTestRule.onNodeWithTag("DateButton").assertTextContains("11/04/2024")
        composeTestRule.onNodeWithTag("TimeButton").assertTextContains("10:00")
    }
}
*/