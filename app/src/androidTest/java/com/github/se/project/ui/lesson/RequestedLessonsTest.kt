package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import com.github.se.project.model.profile.*


/*class LessonsRequestedScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private lateinit var navigationActions: NavigationActions

  private val mockTutorProfile =
      Profile(
          uid = "100",
          googleUid = "150",
          firstName = "Romeo",
          lastName = "Tutor",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.PHYSICS, Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 30)
  private val currentUserFlow = MutableStateFlow(mockTutorProfile)

  private val mockLessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T10:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T11:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0))
  private val requestedLessonsFlow = MutableStateFlow(mockLessons)

  @Before
  fun setup() {

    // Repositories
    profilesRepository = mock(ProfilesRepository::class.java)
    lessonRepository = mock(LessonRepository::class.java)

    // Initialize the Profile and lesson ViewModel with a spy
    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
    listProfilesViewModel = spy(listProfilesViewModel)

    lessonViewModel = LessonViewModel(lessonRepository)
    lessonViewModel = spy(lessonViewModel)

    navigationActions =
        Mockito.mock(NavigationActions::class.java).apply {
          `when`(currentRoute()).thenReturn(Route.FIND_STUDENT)
        }

    doReturn(requestedLessonsFlow).`when`(lessonViewModel).requestedLessons
    doNothing().`when`(lessonRepository).getAllRequestedLessons(any(), any())

    doReturn(currentUserFlow).`when`(listProfilesViewModel).currentProfile

    doNothing().`when`(profilesRepository).getProfiles(any(), any())
  }

  @Test
  fun testScreenComponentsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify Top Bar and Bottom Navigation are displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
  }

  @Test
  fun testDatePickerButtonFunctionality() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Click on the DatePicker to ensure it can be interacted with
    composeTestRule.onNodeWithTag("datePicker").performClick()
  }

  @Test
  fun testLessonsFilteredBySelectedDate() {
    // Set the selected date to filter lessons
    val filteredDate = "2024-10-10"

    // Change the lessons to include only those that match the date
    requestedLessonsFlow.value = mockLessons.filter { it.timeSlot.contains(filteredDate) }

    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }
    // Verify that only the filtered lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
  }

  @Test
  fun testBottomNavigationSelection() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Interact with Bottom Navigation and verify navigation action
    composeTestRule.onNodeWithTag("Find a Student").performClick()

    // Verify using the actual TopLevelDestination object
    verify(navigationActions).navigateTo(TopLevelDestinations.TUTOR)
  }

  @Test
  fun testNoLessonsMessageDisplayedWhenEmpty() {
    requestedLessonsFlow.value = emptyList() // Set no lessons

    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify "No lessons available" message or any placeholder is displayed
    composeTestRule.onNodeWithTag("noLessonsMessage").assertIsDisplayed()
  }
}*/
