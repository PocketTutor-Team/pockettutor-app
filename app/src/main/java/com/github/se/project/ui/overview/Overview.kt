package com.github.se.project.ui.overview

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.notification.PushNotificationPermissionHandler
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.ExpandableLessonSection
import com.github.se.project.ui.components.LessonReviewDialog
import com.github.se.project.ui.components.SectionInfo
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_STUDENT
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_TUTOR
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.navigation.TopLevelDestinations
import com.github.se.project.utils.formatDate
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The HomeScreen composable displays the main interface for the application. It fetches lessons,
 * manages navigation, and shows lessons based on the user's profile and role.
 *
 * @param listProfileViewModel ViewModel to manage user profiles.
 * @param lessonViewModel ViewModel to manage lessons.
 * @param chatViewModel ViewModel for chat functionality.
 * @param navigationActions Handles navigation actions between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    listProfileViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    networkStatusViewModel: NetworkStatusViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val currentProfile = listProfileViewModel.currentProfile.collectAsState().value
  val lessons = lessonViewModel.currentUserLessons.collectAsState().value
  val cancelledLessons = lessonViewModel.cancelledLessons.collectAsState().value

  // Fetch lessons based on the role
  when (currentProfile?.role) {
    Role.TUTOR -> lessonViewModel.getLessonsForTutor(currentProfile.uid) // Fetch lessons for tutor
    Role.STUDENT ->
        lessonViewModel.getLessonsForStudent(currentProfile.uid) // Fetch lessons for student
    Role.UNKNOWN ->
        Toast.makeText(context, "Unknown Profile Role", Toast.LENGTH_SHORT)
            .show() // Show error if the role is unknown
    null ->
        Toast.makeText(context, "No Profile Found", Toast.LENGTH_SHORT)
            .show() // Show error if no profile is found
  }

  // Connect chat for the current profile
  LaunchedEffect(currentProfile) {
    if (currentProfile != null) {
      chatViewModel.connectUser(currentProfile) // Establish a chat connection for the user
    }
  }

  // Define navigation items based on user role
  val navigationItems =
      when (currentProfile?.role) {
        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR // Navigation options for tutors
        Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT // Navigation options for students
        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT // Default to student navigation options
      }

  // Define behavior for lesson item clicks based on role and lesson status
  val onLessonClick = { lesson: Lesson ->
    if (currentProfile?.role == Role.STUDENT) {
      when (lesson.status) {
        LessonStatus.STUDENT_REQUESTED -> {
          if (lesson.tutorUid.isNotEmpty()) { // "Waiting for your confirmation" section
            lessonViewModel.selectLesson(lesson)
            navigationActions.navigateTo(Screen.TUTOR_MATCH)
          } else { // "Waiting for a Tutor proposal" section
            lessonViewModel.selectLesson(lesson)
            navigationActions.navigateTo(Screen.EDIT_REQUESTED_LESSON)
          }
        }
        LessonStatus.CONFIRMED,
        LessonStatus.INSTANT_CONFIRMED -> { // "Upcoming Lessons" section
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
        }
        LessonStatus.INSTANT_REQUESTED -> { // "Pending instant Lesson" section
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.EDIT_REQUESTED_LESSON)
        }
        LessonStatus.PENDING_TUTOR_CONFIRMATION -> { // "Waiting for the Tutor Confirmation" section
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
        }
        else -> {}
      }
    } else {
      when (lesson.status) {
        LessonStatus.PENDING_TUTOR_CONFIRMATION -> {
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.TUTOR_LESSON_RESPONSE)
        }
        LessonStatus.STUDENT_REQUESTED -> {
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
        }
        LessonStatus.CONFIRMED -> {
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
        }
        LessonStatus.INSTANT_CONFIRMED -> {
          lessonViewModel.selectLesson(lesson)
          navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
        }
        else -> {}
      }
    }
  }
  var notificationchecked by remember { mutableStateOf(false) }
  PushNotificationPermissionHandler { isGranted -> notificationchecked = true }

  // Define the screen layout using a Scaffold
  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar").padding(top = 8.dp),
            title = {
              Text(
                  text = "Welcome, ${currentProfile?.firstName}",
                  style =
                      MaterialTheme.typography
                          .titleLarge) // Display the user's first name in the top bar
            },
            actions = {
              if (currentProfile?.role == Role.STUDENT) {
                // Icon to navigate to the list of favorite tutor profiles screen
                IconButton(
                    onClick = { navigationActions.navigateTo(Screen.FAVORITE_TUTORS) },
                    modifier = Modifier.testTag("favorite_tutors_button")) {
                      Icon(
                          imageVector = Icons.Default.Star,
                          contentDescription = "Favorite Tutors Icon",
                          Modifier.testTag("favorite_tutor_icon").size(32.dp))
                    }
              }

              IconButton(onClick = { navigationActions.navigateTo(Screen.PROFILE) }) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Profile Icon",
                    Modifier.testTag("Profile Icon")
                        .size(32.dp)) // Icon to navigate to the profile screen
              }
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route ->
              if (route == TopLevelDestinations.STUDENT) {
                lessonViewModel
                    .unselectLesson() // Unselect the lesson when navigating to student tab
              }
              navigationActions.navigateTo(route) // Navigate to selected route
            },
            tabList = navigationItems, // List of navigation items based on role
            selectedItem = navigationActions.currentRoute(),
            networkStatusViewModel = networkStatusViewModel) // Currently selected navigation item
      }) { paddingValues ->

        // Display the content or appropriate fallback based on the current profile
        currentProfile?.let { profile ->
          if (cancelledLessons.isNotEmpty()) {
            CancellationAlerts(
                profile = profile,
                lessons = cancelledLessons,
                listProfilesViewModel = listProfileViewModel,
                lessonViewModel = lessonViewModel)
          } else {
            if (lessons.any { it.status != LessonStatus.COMPLETED }) {
              LessonsContent(
                  profile = profile,
                  lessons = lessons,
                  onClick = onLessonClick,
                  paddingValues = paddingValues,
                  listProfilesViewModel = listProfileViewModel,
                  lessonViewModel = lessonViewModel)
            } else {
              EmptyLessonsState(paddingValues, lessonViewModel, profile)
            }
          }
        }
            ?: NoProfileFoundScreen(
                context, navigationActions) // Show error screen if no profile is assigned
  }
}

/**
 * Displays lessons and their states, organized by sections.
 *
 * @param profile The current user's profile.
 * @param lessons List of lessons to display.
 * @param onClick Callback for when a lesson is clicked.
 * @param paddingValues Padding for the content.
 * @param listProfilesViewModel ViewModel for managing profiles.
 * @param lessonViewModel ViewModel for managing lessons.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LessonsContent(
    profile: Profile,
    lessons: List<Lesson>,
    onClick: (Lesson) -> Unit,
    paddingValues: PaddingValues,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel
) {
  var refreshing by remember { mutableStateOf(false) }
  val refreshScope = rememberCoroutineScope()

  // Refresh function to reload lessons based on profile role
  fun refresh() =
      refreshScope.launch {
        refreshing = true
        try {
          when (profile.role) {
            Role.TUTOR -> lessonViewModel.getLessonsForTutor(profile.uid)
            Role.STUDENT -> lessonViewModel.getLessonsForStudent(profile.uid)
            else -> {}
          }
        } finally {
          delay(1000)
          refreshing = false
        }
      }

  val pullRefreshState =
      rememberPullRefreshState(refreshing = refreshing, onRefresh = { refresh() })

  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues)
              .padding(horizontal = 16.dp)
              .pullRefresh(pullRefreshState)) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
          if (profile.role == Role.TUTOR) {
            TutorSections(lessons, onClick, listProfilesViewModel)
          } else {
            StudentSections(lessons, onClick, listProfilesViewModel, lessonViewModel)
          }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary)
      }
}

@Composable
private fun TutorSections(
    lessons: List<Lesson>,
    onClick: (Lesson) -> Unit,
    listProfilesViewModel: ListProfilesViewModel
) {
  val sections = mutableListOf<SectionInfo>()

  if (lessons.any { it.status == LessonStatus.INSTANT_CONFIRMED }) {
    sections.add(
        SectionInfo(
            "Upcoming Instant Lesson",
            LessonStatus.INSTANT_CONFIRMED,
            ImageVector.vectorResource(
                id = R.drawable.bolt_24dp_000000_fill1_wght400_grad0_opsz24)))
  }
  sections.add(
      SectionInfo(
          "Waiting for your Confirmation",
          LessonStatus.PENDING_TUTOR_CONFIRMATION,
          Icons.Default.Notifications))
  sections.add(
      SectionInfo(
          "Waiting for the Student Confirmation",
          LessonStatus.STUDENT_REQUESTED,
          ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)))
  sections.add(SectionInfo("Upcoming Lessons", LessonStatus.CONFIRMED, Icons.Default.Check))

  LessonSections(sections, lessons, true, onClick, listProfilesViewModel)
}

@Composable
private fun StudentSections(
    lessons: List<Lesson>,
    onClick: (Lesson) -> Unit,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel
) {
  var lessonToReview by remember { mutableStateOf<Lesson?>(null) }
  val sections = mutableListOf<SectionInfo>()

  // Check for lessons that need review
  LaunchedEffect(lessons) {
    if (lessonToReview == null) {
      val pendingReview = lessons.find { lesson -> lesson.status == LessonStatus.PENDING_REVIEW }
      lessonToReview = pendingReview
    }
  }

  // Show review dialog if needed
  lessonToReview?.let { lesson ->
    LessonReviewDialog(
        lesson = lesson,
        initialRating = null,
        onDismiss = {
          val updatedLesson = lesson.copy(status = LessonStatus.COMPLETED)
          lessonViewModel.updateLesson(updatedLesson) {
            lessonViewModel.getLessonsForStudent(lesson.studentUid)
          }
          lessonToReview = null
        },
        onSubmitReview = { rating, comment ->
          val updatedLesson =
              lesson.copy(
                  status = LessonStatus.COMPLETED,
                  rating = LessonRating(grade = rating, comment = comment, date = Timestamp.now()))
          lessonViewModel.updateLesson(updatedLesson) {
            lessonViewModel.getLessonsForStudent(lesson.studentUid)
          }
          lessonToReview = null // Important: reset after submission
        },
        tutor = lesson.tutorUid.getOrNull(0)?.let { listProfilesViewModel.getProfileById(it) })
  }

  if (lessons.any { it.status == LessonStatus.INSTANT_REQUESTED }) {
    sections.add(
        SectionInfo(
            "Pending instant Lesson",
            LessonStatus.INSTANT_REQUESTED,
            ImageVector.vectorResource(id = R.drawable.bolt_24dp_000000_fill1_wght400_grad0_opsz24),
            true))
  }
  if (lessons.any { it.status == LessonStatus.INSTANT_CONFIRMED }) {
    sections.add(
        SectionInfo(
            "Upcoming Instant Lesson",
            LessonStatus.INSTANT_CONFIRMED,
            ImageVector.vectorResource(
                id = R.drawable.bolt_24dp_000000_fill1_wght400_grad0_opsz24)))
  }
  sections.add(
      SectionInfo(
          "Waiting for your Confirmation",
          LessonStatus.STUDENT_REQUESTED,
          Icons.Default.Notifications))
  sections.add(
      SectionInfo(
          "Waiting for a Tutor proposal",
          LessonStatus.STUDENT_REQUESTED,
          ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
          true))
  sections.add(
      SectionInfo(
          "Waiting for the Tutor Confirmation",
          LessonStatus.PENDING_TUTOR_CONFIRMATION,
          ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)))
  sections.add(SectionInfo("Upcoming Lessons", LessonStatus.CONFIRMED, Icons.Default.Check))

  LessonSections(sections, lessons, false, onClick, listProfilesViewModel)
}

@Composable
private fun LessonSections(
    sections: List<SectionInfo>,
    lessons: List<Lesson>,
    isTutor: Boolean,
    onClick: (Lesson) -> Unit,
    listProfilesViewModel: ListProfilesViewModel
) {
  sections.forEach { section ->
    val sectionLessons =
        lessons.filter {
          it.status == section.status && it.tutorUid.isEmpty() == section.tutorEmpty
        }

    ExpandableLessonSection(
        section = section,
        lessons = sectionLessons,
        isTutor = isTutor,
        onClick = onClick,
        listProfilesViewModel = listProfilesViewModel)
    Spacer(modifier = Modifier.height(16.dp))
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EmptyLessonsState(
    paddingValues: PaddingValues,
    lessonViewModel: LessonViewModel,
    profile: Profile
) {
  var refreshing by remember { mutableStateOf(false) }
  val refreshScope = rememberCoroutineScope()

  fun refresh() =
      refreshScope.launch {
        refreshing = true
        when (profile.role) {
          Role.TUTOR -> lessonViewModel.getLessonsForTutor(profile.uid)
          Role.STUDENT -> lessonViewModel.getLessonsForStudent(profile.uid)
          else -> {}
        }
        delay(1000)
        refreshing = false
      }

  LaunchedEffect(Unit) {
    while (true) {
      refresh()
      delay(30000) // Refresh every 30 seconds
    }
  }

  val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

  Box(
      modifier = Modifier.fillMaxSize().padding(paddingValues).pullRefresh(pullRefreshState),
      contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier.fillMaxWidth().padding(32.dp).verticalScroll(rememberScrollState())) {
              Image(
                  painter = painterResource(id = R.drawable.logopocket),
                  contentDescription = null,
                  modifier = Modifier.size(148.dp))
              Text(
                  text = "No active lessons",
                  style = MaterialTheme.typography.titleLarge,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.testTag("noLessonsText"))
              Text(
                  text = "Your lessons will appear here once you have some scheduled",
                  style = MaterialTheme.typography.bodyMedium,
                  textAlign = TextAlign.Center,
                  color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary)
      }
}
/**
 * Displays an error screen when no profile is found for the current user.
 *
 * @param context The context to display the error message.
 * @param navigationActions Navigation actions to allow users to go back to the home screen.
 */
@Composable
fun NoProfileFoundScreen(context: Context, navigationActions: NavigationActions) {
  // Center the contents within the screen
  Column(
      modifier = Modifier.fillMaxSize().testTag("noProfileScreen"), // Take up full screen space
      verticalArrangement = Arrangement.Center, // Center vertically
      horizontalAlignment = Alignment.CenterHorizontally) { // Center horizontally

        // Display an error message notifying the user that no profile is assigned
        Text(
            text = "No profile is currently assigned to the current user.",
            modifier = Modifier.testTag("noProfileText")) // Add a test tag for testing purposes

        Spacer(modifier = Modifier.height(8.dp)) // Add a space between the text and the button

        // Provide a button to navigate back to the home screen
        Button(
            onClick = {
              navigationActions.navigateTo(
                  Screen.HOME) // Navigate back to the home screen when clicked
            },
            modifier = Modifier.testTag("goBackHomeButton")) { // Add a test tag for the button
              Text(text = "Go back to HOME screen") // Button text
        }
      }
}

fun Lesson.shouldRequestReview(): Boolean {
  if (this.status != LessonStatus.CONFIRMED && this.status != LessonStatus.INSTANT_CONFIRMED)
      return false
  if (this.rating != null) return false

  try {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
    val lessonDateTime = LocalDateTime.parse(this.timeSlot, formatter)
    val oneHourAfterLesson = lessonDateTime.plusHours(1)

    return now.isAfter(oneHourAfterLesson)
  } catch (e: Exception) {
    Log.e("Lesson", "Error parsing date or calculating time", e)
    return false
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancellationAlerts(
    profile: Profile,
    lessons: List<Lesson>,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel
) {
  lessons.forEach { lesson ->
    val isTutor = profile.role == Role.TUTOR

    val tutor =
        if (isTutor) profile
        else
            listProfilesViewModel.getProfileById(lesson.tutorUid.first())
                ?: return Text("Tutor not found")

    val student =
        if (isTutor)
            listProfilesViewModel.getProfileById(lesson.studentUid)
                ?: return Text("Student not found")
        else profile

    var showCancelDialog = true // Show the alert dialog

    if (showCancelDialog) {
      AlertDialog(
          modifier = Modifier.testTag("cancelledLessonDialog"),
          onDismissRequest = { showCancelDialog = false },
          title = {
            Text(
                text = "Lesson Cancelled by the ${if (isTutor) "Student" else "Tutor"}",
                modifier = Modifier.testTag("cancelledLessonDialogTitle"))
          },
          text = {
            Text(
                text =
                    if (isTutor)
                        "Be careful! Your lesson with ${student.firstName} ${student.lastName} has been cancelled. It was programmed for ${formatDate(lesson.timeSlot)}."
                    else
                        "Be careful! Your lesson with ${tutor.firstName} ${tutor.lastName} has been cancelled. It was programmed for ${formatDate(lesson.timeSlot)}.",
                modifier = Modifier.testTag("cancelledLessonDialogText"))
          },
          confirmButton = {
            Button(
                modifier = Modifier.testTag("cancelledLessonDialogConfirmButton"),
                onClick = {
                  lessonViewModel.deleteLesson(
                      lessonId = lesson.id,
                      onComplete = {
                        if (isTutor)
                            lessonViewModel.getLessonsForTutor(
                                tutor.uid, { showCancelDialog = false })
                        else
                            lessonViewModel.getLessonsForStudent(
                                student.uid, { showCancelDialog = false })
                      })
                }) {
                  Text("OK")
                }
          },
      )
    }
  }
}
