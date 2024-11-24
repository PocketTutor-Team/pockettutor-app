package com.github.se.project.ui.overview

import android.content.Context
import android.widget.Toast
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
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.*
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.navigation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    listProfileViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    chatViewModel: ChatViewModel,
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

  LaunchedEffect(currentProfile) {
    if (currentProfile != null) {
      chatViewModel.connect(currentProfile)
    }
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
      } else if (lesson.status == LessonStatus.CONFIRMED) {
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
        when (profile.role) {
          Role.TUTOR -> lessonViewModel.getLessonsForTutor(profile.uid)
          Role.STUDENT -> lessonViewModel.getLessonsForStudent(profile.uid)
          else -> {}
        }
        delay(1000)
        refreshing = false
      }

  val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues)
              .padding(horizontal = 16.dp)
              .testTag("lessonsColumn")) {
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .verticalScroll(rememberScrollState())
                      .pullRefresh(pullRefreshState)) {
                if (profile.role == Role.TUTOR) {
                  TutorSections(lessons, onClick, listProfilesViewModel)
                } else {
                  StudentSections(lessons, onClick, listProfilesViewModel)
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
                "Waiting for your Confirmation",
                LessonStatus.PENDING_TUTOR_CONFIRMATION,
                Icons.Default.Notifications),
            SectionInfo(
                "Waiting for the Student Confirmation",
                LessonStatus.STUDENT_REQUESTED,
                ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)),
            SectionInfo(
                "Instant Lesson", LessonStatus.INSTANT_CONFIRMED, Icons.Default.Notifications),
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
    listProfilesViewModel: ListProfilesViewModel
) {
  val sections = mutableListOf<SectionInfo>()
  if (lessons.any { it.status == LessonStatus.INSTANT_REQUESTED }) {
    sections.add(
        SectionInfo(
            "Pending instant Lesson",
            LessonStatus.INSTANT_REQUESTED,
            ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
            true))
  }
  if (lessons.any { it.status == LessonStatus.INSTANT_CONFIRMED }) {
    sections.add(
        SectionInfo("Instant Lesson", LessonStatus.INSTANT_CONFIRMED, Icons.Default.Notifications))
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
    val sectionLessons = lessons.filter { it.status == section.status }

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
  var expanded by remember { mutableStateOf(lessons.isNotEmpty()) }

  Card(
      modifier = Modifier.fillMaxWidth().testTag("section_${section.title}"),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column {
          ListItem(
              headlineContent = {
                Text(section.title, style = MaterialTheme.typography.titleMedium)
              },
              colors =
                  ListItemDefaults.colors(
                      containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              leadingContent = {
                Icon(section.icon, null, tint = MaterialTheme.colorScheme.primary)
              },
              trailingContent = {
                IconButton(onClick = { expanded = !expanded }) {
                  Icon(
                      if (expanded) Icons.Default.KeyboardArrowDown
                      else Icons.Default.KeyboardArrowLeft,
                      contentDescription = if (expanded) "Collapse" else "Expand")
                }
              },
              modifier = Modifier.clickable { expanded = !expanded })

          if (expanded) {
            DisplayLessons(
                lessons = lessons,
                statusFilter = section.status,
                isTutor = isTutor,
                tutorEmpty = section.tutorEmpty,
                onCardClick = onClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
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

private data class SectionInfo(
    val title: String,
    val status: LessonStatus,
    val icon: ImageVector,
    val tutorEmpty: Boolean = false
)
