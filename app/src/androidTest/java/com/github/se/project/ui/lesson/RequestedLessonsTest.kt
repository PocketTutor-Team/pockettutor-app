package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.navigation.TopLevelDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class LessonsRequestedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var listProfilesViewModel: ListProfilesViewModel
    private lateinit var lessonViewModel: LessonViewModel
    private lateinit var navigationActions: NavigationActions
    private lateinit var profilesRepository: ProfilesRepository
    private lateinit var lessonRepository: LessonRepository

    private val testProfile = Profile(
        uid = "uid",
        googleUid = "googleUid",
        firstName = "firstName",
        lastName = "lastName",
        phoneNumber = "phoneNumber",
        role = Role.TUTOR,
        section = Section.AR,
        academicLevel = AcademicLevel.BA1,
        languages = listOf(Language.ENGLISH),
        subjects = listOf(Subject.ANALYSIS),
        schedule = List(7) { List(12) { 0 } },
        price = 0
    )

    private val testLessons = listOf(
        Lesson(
            id = "1",
            title = "Math Help",
            description = "Help with calculus",
            subject = Subject.ANALYSIS,
            languages = listOf(Language.ENGLISH),
            tutorUid = "",
            studentUid = "student123",
            minPrice = 20.0,
            maxPrice = 30.0,
            price = 25.0,
            timeSlot = "10/10/2024T10:00:00",
            status = LessonStatus.STUDENT_REQUESTED
        ),
        Lesson(
            id = "2",
            title = "Physics Tutorial",
            description = "Mechanics review",
            subject = Subject.PHYSICS,
            languages = listOf(Language.ENGLISH),
            tutorUid = "",
            studentUid = "student456",
            minPrice = 25.0,
            maxPrice = 35.0,
            price = 30.0,
            timeSlot = "11/10/2024T14:00:00",
            status = LessonStatus.STUDENT_REQUESTED
        )
    )

    private val requestedLessonsFlow = MutableStateFlow(testLessons)

    @Before
    fun setup() {
        // Create mocks
        profilesRepository = mock(ProfilesRepository::class.java)
        lessonRepository = mock(LessonRepository::class.java)
        navigationActions = mock(NavigationActions::class.java)

        // Setup ProfilesRepository
        doNothing().`when`(profilesRepository).init(any())
        `when`(profilesRepository.getNewUid()).thenReturn("new-uid")
        doAnswer { invocation ->
            val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
            onSuccess(listOf(testProfile))
        }.`when`(profilesRepository).getProfiles(any(), any())

        // Setup LessonRepository
        doNothing().`when`(lessonRepository).init(any())
        doAnswer { invocation ->
            val onSuccess = invocation.arguments[0] as (List<Lesson>) -> Unit
            onSuccess(testLessons)
        }.`when`(lessonRepository).getAllRequestedLessons(any(), any())

        // Create ViewModels
        listProfilesViewModel = ListProfilesViewModel(profilesRepository).apply {
            setCurrentProfile(testProfile)
        }

        lessonViewModel = spy(LessonViewModel(lessonRepository))
        `when`(lessonViewModel.requestedLessons).thenReturn(requestedLessonsFlow)

        // Setup NavigationActions
        `when`(navigationActions.currentRoute()).thenReturn(Route.FIND_STUDENT)
    }

    @Test
    fun testTopBarComponents() {
        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithTag("topBar").assertExists()
        composeTestRule.onNodeWithTag("screenTitle").assertExists()
        composeTestRule.onNodeWithTag("datePicker").assertExists()
        composeTestRule.onNodeWithTag("filterButton").assertExists()
    }

    @Test
    fun testEmptyStateDisplay() {
        requestedLessonsFlow.value = emptyList()

        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithText("No lessons available").assertExists()
        composeTestRule.onNodeWithText("Try adjusting your filters or check back later").assertExists()
    }

    @Test
    fun testLessonsListDisplay() {
        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithTag("lessonsList").assertExists()
        composeTestRule.onNodeWithText("Math Help").assertExists()
        composeTestRule.onNodeWithText("Physics Tutorial").assertExists()
    }

    @Test
    fun testFilterDialog() {
        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithTag("filterButton").performClick()
        composeTestRule.onNodeWithText("Filter by subject").assertExists()
        composeTestRule.onNodeWithText("Clear filter").assertExists()
        composeTestRule.onNodeWithText("Cancel").assertExists()
    }

    @Test
    fun testBottomNavigation() {
        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists()
        composeTestRule.onNodeWithTag("Find a Student").performClick()
        verify(navigationActions).navigateTo(TopLevelDestinations.TUTOR)
    }

    @Test
    fun testGetAllRequestedLessonsCalledOnLaunch() {
        composeTestRule.setContent {
            RequestedLessonsScreen(
                listProfilesViewModel = listProfilesViewModel,
                lessonViewModel = lessonViewModel,
                navigationActions = navigationActions
            )
        }

        verify(lessonViewModel).getAllRequestedLessons()
    }
}