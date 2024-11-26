package com.github.se.project.ui.overview

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.github.se.project.model.lesson.*
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.components.LessonReviewDialog
import com.github.se.project.ui.components.isInstant
import com.github.se.project.ui.navigation.*
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    listProfileViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val currentProfile = listProfileViewModel.currentProfile.collectAsState().value
  val lessons = lessonViewModel.currentUserLessons.collectAsState().value

  // Fetch lessons based on the role
  when (currentProfile?.role) {
    Role.TUTOR -> lessonViewModel.getLessonsForTutor(currentProfile.uid)
    Role.STUDENT -> lessonViewModel.getLessonsForStudent(currentProfile.uid)
    Role.UNKNOWN -> Toast.makeText(context, "Unknown Profile Role", Toast.LENGTH_SHORT).show()
    null -> Toast.makeText(context, "No Profile Found", Toast.LENGTH_SHORT).show()
  }

  val navigationItems =
      when (currentProfile?.role) {
        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
        Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
      }

  val onLessonClick = { lesson: Lesson ->
    if (currentProfile?.role == Role.STUDENT) {
      if (lesson.status == LessonStatus.STUDENT_REQUESTED && lesson.tutorUid.isNotEmpty()) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.TUTOR_MATCH)
      } else if (lesson.status == LessonStatus.STUDENT_REQUESTED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.EDIT_REQUESTED_LESSON)
      } else if (lesson.status == LessonStatus.CONFIRMED ||
          lesson.status == LessonStatus.INSTANT_CONFIRMED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
      } else if (lesson.status == LessonStatus.INSTANT_REQUESTED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.EDIT_REQUESTED_LESSON)
      } else if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
      }
    } else {
      if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.TUTOR_LESSON_RESPONSE)
      } else if (lesson.status == LessonStatus.CONFIRMED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
      } else if (lesson.status == LessonStatus.INSTANT_CONFIRMED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
      } else if (lesson.status == LessonStatus.STUDENT_REQUESTED) {
        lessonViewModel.selectLesson(lesson)
        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
      }
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar").padding(top = 8.dp),
            title = {
              Text(
                  text = "Welcome, ${currentProfile?.firstName}",
                  style = MaterialTheme.typography.titleLarge)
            },
            actions = {
              IconButton(onClick = { navigationActions.navigateTo(Screen.PROFILE) }) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Profile Icon",
                    Modifier.testTag("Profile Icon").size(32.dp))
              }
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route ->
              if (route == TopLevelDestinations.STUDENT) {
                lessonViewModel.unselectLesson()
              }
              navigationActions.navigateTo(route)
            },
            tabList = navigationItems,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        currentProfile?.let { profile ->
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
        } ?: NoProfileFoundScreen(context, navigationActions)
      }
}

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
  val sections =
      if (lessons.any { it.status == LessonStatus.INSTANT_CONFIRMED }) {
        listOf(
            SectionInfo(
                "Instant Lesson",
                LessonStatus.INSTANT_CONFIRMED,
                ImageVector.vectorResource(
                    id = R.drawable.bolt_24dp_000000_fill1_wght400_grad0_opsz24)),
            SectionInfo(
                "Waiting for your Confirmation",
                LessonStatus.PENDING_TUTOR_CONFIRMATION,
                Icons.Default.Notifications),
            SectionInfo(
                "Waiting for the Student Confirmation",
                LessonStatus.STUDENT_REQUESTED,
                ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)),
            SectionInfo("Upcoming Lessons", LessonStatus.CONFIRMED, Icons.Default.Check))
      } else {
        listOf(
            SectionInfo(
                "Waiting for your Confirmation",
                LessonStatus.PENDING_TUTOR_CONFIRMATION,
                Icons.Default.Notifications),
            SectionInfo(
                "Waiting for the Student Confirmation",
                LessonStatus.STUDENT_REQUESTED,
                ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)),
            SectionInfo("Upcoming Lessons", LessonStatus.CONFIRMED, Icons.Default.Check))
      }

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
    if (lessonToReview == null) { // Add this condition to prevent continuous updates
      val pendingReview =
          lessons.find { lesson ->
            (lesson.status == LessonStatus.CONFIRMED ||
                lesson.status == LessonStatus.INSTANT_CONFIRMED) &&
                lesson.shouldRequestReview() &&
                lesson.rating == null // Ensure we don't prompt for already rated lessons
          }
      lessonToReview = pendingReview
    }
  }

  // Show review dialog if needed
  lessonToReview?.let { lesson ->
    LessonReviewDialog(
        lesson = lesson,
        initialRating = null,
        onDismiss = {
          lessonToReview = null // Important: reset the lessonToReview
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
        })
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
            "Instant Lesson",
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

@Composable
private fun ExpandableLessonSection(
    section: SectionInfo,
    lessons: List<Lesson>,
    isTutor: Boolean,
    onClick: (Lesson) -> Unit,
    listProfilesViewModel: ListProfilesViewModel
) {
  val isInstant = lessons.any { isInstant(it) }
  var expanded by remember { mutableStateOf(if (isInstant) true else lessons.isNotEmpty()) }

  LaunchedEffect(lessons.isNotEmpty()) { expanded = lessons.isNotEmpty() }

  val infiniteTransition = rememberInfiniteTransition(label = "iconBlink")
  val alpha by
      infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 0.3f,
          animationSpec =
              infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse),
          label = "blinkAlpha")

  Card(
      modifier = Modifier.fillMaxWidth().testTag("section_${section.title}"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  if (isInstant) MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f)
                  else MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column {
          ListItem(
              headlineContent = {
                Text(section.title, style = MaterialTheme.typography.titleMedium)
              },
              colors =
                  ListItemDefaults.colors(
                      containerColor =
                          if (isInstant) MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.4f)
                          else MaterialTheme.colorScheme.surfaceContainerLowest),
              leadingContent = {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isInstant) alpha else 1f))
              },
              trailingContent =
                  if (!isInstant) {
                    {
                      IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowLeft,
                            contentDescription = if (expanded) "Collapse" else "Expand")
                      }
                    }
                  } else null,
              modifier =
                  if (!isInstant) {
                    Modifier.clickable { expanded = !expanded }
                  } else Modifier)

          if (expanded) {
            DisplayLessons(
                lessons = lessons,
                statusFilter = section.status,
                isTutor = isTutor,
                tutorEmpty = section.tutorEmpty,
                onCardClick = onClick,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                listProfilesViewModel = listProfilesViewModel)
          }
        }
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

@Composable
fun NoProfileFoundScreen(context: Context, navigationActions: NavigationActions) {
  // Display an error message when no profile is assigned
  Column(
      modifier = Modifier.fillMaxSize().testTag("noProfileScreen"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "No profile is currently assigned to the current user.",
            modifier = Modifier.testTag("noProfileText"))
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navigationActions.navigateTo(Screen.HOME) },
            modifier = Modifier.testTag("goBackHomeButton")) {
              Text(text = "Go back to HOME screen")
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

private data class SectionInfo(
    val title: String,
    val status: LessonStatus,
    val icon: ImageVector,
    val tutorEmpty: Boolean = false
)
