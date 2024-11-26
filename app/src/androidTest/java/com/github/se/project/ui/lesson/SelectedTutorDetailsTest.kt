// package com.github.se.project.ui.lesson
//
// import androidx.compose.ui.test.assertIsDisplayed
// import androidx.compose.ui.test.assertIsNotDisplayed
// import androidx.compose.ui.test.junit4.createComposeRule
// import androidx.compose.ui.test.onNodeWithTag
// import androidx.compose.ui.test.performClick
// import androidx.navigation.NavHostController
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.github.se.project.model.lesson.Lesson
// import com.github.se.project.model.lesson.LessonRepository
// import com.github.se.project.model.lesson.LessonStatus
// import com.github.se.project.model.lesson.LessonViewModel
// import com.github.se.project.model.profile.AcademicLevel
// import com.github.se.project.model.profile.Comment
// import com.github.se.project.model.profile.Language
// import com.github.se.project.model.profile.ListProfilesViewModel
// import com.github.se.project.model.profile.Profile
// import com.github.se.project.model.profile.ProfilesRepository
// import com.github.se.project.model.profile.Rating
// import com.github.se.project.model.profile.Role
// import com.github.se.project.model.profile.Section
// import com.github.se.project.model.profile.Subject
// import com.github.se.project.ui.navigation.NavigationActions
// import com.github.se.project.ui.navigation.Screen
// import com.google.firebase.Timestamp
// import java.util.Calendar
// import kotlinx.coroutines.flow.MutableStateFlow
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.runner.RunWith
// import org.mockito.Mockito.anyString
// import org.mockito.Mockito.doNothing
// import org.mockito.Mockito.mock
// import org.mockito.Mockito.spy
// import org.mockito.kotlin.any
// import org.mockito.kotlin.verify
// import org.mockito.kotlin.whenever
//
// @RunWith(AndroidJUnit4::class)
// class SelectedTutorDetailsTest {
//  @get:Rule val composeTestRule = createComposeRule()
//
//  private lateinit var profilesRepository: ProfilesRepository
//  private lateinit var listProfilesViewModel: ListProfilesViewModel
//
//  private lateinit var lessonRepository: LessonRepository
//  private lateinit var lessonViewModel: LessonViewModel
//
//  private lateinit var navigationActions: NavigationActions
//  private lateinit var navController: NavHostController
//
//  private val studentProfileFlow =
//      MutableStateFlow(
//          Profile(
//              uid = "uid",
//              googleUid = "googleUid",
//              firstName = "John",
//              lastName = "Student",
//              phoneNumber = "1234567890",
//              role = Role.STUDENT,
//              section = Section.GM,
//              academicLevel = AcademicLevel.MA2))
//
//  private val calendar = Calendar.getInstance().apply { set(2024, Calendar.OCTOBER, 19, 10, 0, 0)
// }
//  private val tutorProfileFlow =
//      MutableStateFlow(
//          Profile(
//              uid = "uid",
//              googleUid = "googleUid",
//              firstName = "Elena",
//              lastName = "Tutor",
//              phoneNumber = "1234567890",
//              role = Role.TUTOR,
//              section = Section.IN,
//              academicLevel = AcademicLevel.PhD,
//              description = "I am the best tutor haha",
//              languages = listOf(Language.FRENCH, Language.ENGLISH),
//              subjects = listOf(Subject.ANALYSIS, Subject.ALGEBRA),
//              schedule = List(7) { List(12) { 0 } },
//              price = 30,
//              rating =
//                  Rating(
//                      averageRating = 5.0,
//                      totalRatings = 1,
//                      comments =
//                          mutableListOf(
//                              Comment(
//                                  grade = 5,
//                                  raterUid = studentProfileFlow.value.uid,
//                                  date = Timestamp(calendar.time),
//                                  comment = "Really great tutor!")))))
//
//  private val lessonFlow =
//      MutableStateFlow(
//          Lesson(
//              id = "lessonId",
//              title = "Math Lesson",
//              description = "Algebra",
//              subject = Subject.ANALYSIS,
//              languages = listOf(Language.ENGLISH),
//              tutorUid = listOf("tutor123"),
//              studentUid = "student123",
//              minPrice = 10.0,
//              maxPrice = 50.0,
//              timeSlot = "19/10/2024T10:00:00",
//              status = LessonStatus.MATCHING,
//              latitude = 0.0,
//              longitude = 0.0))
//
//  @Before
//  fun setup() {
//    // Mock dependencies
//    profilesRepository = mock(ProfilesRepository::class.java)
//    lessonRepository = mock(LessonRepository::class.java)
//    navController = mock()
//
//    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
//    listProfilesViewModel = spy(listProfilesViewModel)
//
//    lessonViewModel = LessonViewModel(lessonRepository)
//    lessonViewModel = spy(lessonViewModel)
//
//    navigationActions = NavigationActions(navController)
//    navigationActions = spy(navigationActions)
//
//    // Stub navigation actions
//    doNothing().`when`(navigationActions).navigateTo(anyString())
//
//    // Mock flow properties on ViewModels
//    whenever(listProfilesViewModel.currentProfile).thenReturn(studentProfileFlow)
//    whenever(listProfilesViewModel.selectedProfile).thenReturn(tutorProfileFlow)
//    whenever(lessonViewModel.selectedLesson).thenReturn(lessonFlow)
//
//    // Correctly mock the profiles flow
//
//    // Stub repository methods to simulate successful data retrieval
//    whenever(profilesRepository.init(any())).thenAnswer { invocation ->
//      val onSuccess = invocation.arguments[0] as () -> Unit
//      onSuccess() // Simulate successful initialization
//    }
//
//    whenever(lessonRepository.addLesson(any(), any(), any())).thenAnswer { invocation ->
//      val onSuccess = invocation.arguments[1] as () -> Unit
//      onSuccess() // Simulate a successful update
//    }
//
//    whenever(profilesRepository.getProfiles(any(), any())).thenAnswer {
//      val onSuccess = it.arguments[0] as (List<Profile>) -> Unit
//      onSuccess(listOf(studentProfileFlow.value, tutorProfileFlow.value))
//    }
//
//    listProfilesViewModel.getProfiles()
//  }
//
//  @Test
//  fun everythingIsDisplayed() {
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Check if teh top bar is correctly displayed
//    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("confirmLessonTitle").assertIsDisplayed()
//
//    // Check if the tutor details are correctly displayed
//    composeTestRule.onNodeWithTag("selectedTutorDetailsScreen").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorDetailsCard").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorInfoRow").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorProfilePicture").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorName").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorAcademicInfo").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorPrice").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorRatingLabel").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorRatingIcon").assertIsDisplayed()
//
//    // Check if the tutor description is correctly displayed
//    composeTestRule.onNodeWithTag("tutorDescription").assertIsDisplayed()
//
//    // Check if comments are correctly displayed
//    composeTestRule.onNodeWithTag("tutorCommentsSection").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorCommentsTitle").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("commentCard").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentProfilePicture").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentName").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentCommentDate").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentRatingLabel").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentRatingIcon").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("studentComment").assertIsDisplayed()
//
//    // Check if the confirmation button is correctly displayed
//    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
//  }
//
//  @Test
//  fun errorStateIsDisplayed_whenTutorNotTutor() {
//    // Set tutor profile to student
//    tutorProfileFlow.value = studentProfileFlow.value
//
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Verify the error message is displayed
//    composeTestRule.onNodeWithTag("errorStateColumn").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("errorIcon").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("errorMessage").assertIsDisplayed()
//  }
//
//  @Test
//  fun emptyDescriptionIsDisplayed_whenTutorHasNoDescription() {
//    // Set tutor profile to student
//    tutorProfileFlow.value = tutorProfileFlow.value.copy(description = "")
//
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Verify the empty description message is displayed
//    composeTestRule.onNodeWithTag("tutorDescriptionSection").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorDescriptionEmpty").assertIsDisplayed()
//  }
//
//  @Test
//  fun noReviewsIsDsiplayed_whenTutorHasNoReviews() {
//    // Set tutor profile to student
//    tutorProfileFlow.value = tutorProfileFlow.value.copy(rating = Rating(0.0, 0, mutableListOf()))
//
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Verify the no reviews message is displayed
//    composeTestRule.onNodeWithTag("tutorCommentsSection").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("tutorCommentsEmpty").assertIsDisplayed()
//  }
//
//  @Test
//  fun confirmationButton_displaysAlertDialog() {
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Click the confirmation button
//    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()
//
//    // Verify the alert dialog is displayed
//    composeTestRule.onNodeWithTag("confirmDialog").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("confirmDialogTitle").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("confirmDialogText").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("confirmDialogButton").assertIsDisplayed()
//    composeTestRule.onNodeWithTag("confirmDialogCancelButton").assertIsDisplayed()
//  }
//
//  @Test
//  fun confirmationButton_actsCorrectlyWhenDialogConfirmed() {
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Click the confirmation button
//    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()
//
//    // Click the confirm button on the dialog
//    composeTestRule.onNodeWithTag("confirmDialogButton").assertIsDisplayed().performClick()
//
//    // Verify the navigation action is called
//    verify(navigationActions).navigateTo(Screen.HOME)
//    verify(lessonRepository).addLesson(any(), any(), any())
//  }
//
//  @Test
//  fun confirmationButton_actsCorrectlyWhenDialogCancelled() {
//    composeTestRule.setContent {
//      SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
//    }
//
//    // Click the confirmation button
//    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed().performClick()
//
//    // Click the cancel button on the dialog
//    composeTestRule.onNodeWithTag("confirmDialogCancelButton").assertIsDisplayed().performClick()
//
//    // Verify the dialog is dismissed
//    composeTestRule.onNodeWithTag("confirmDialog").assertIsNotDisplayed()
//    composeTestRule.onNodeWithTag("selectedTutorDetailsScreen").assertIsDisplayed()
//  }
// }
