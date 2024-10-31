package com.github.se.project.ui.overview

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_STUDENT
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_TUTOR
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

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
    navigationActions.navigateTo(Screen.EDIT_REQUESTED_LESSON + "/${lesson.id}")
  }

  Scaffold(
      topBar = {
        Row(
            modifier =
                Modifier.testTag("topBar").fillMaxWidth().padding(16.dp).testTag("topBarRow"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "Your dashboard",
                  style = MaterialTheme.typography.headlineMedium,
                  modifier = Modifier.testTag("dashboardTitle"))
              IconButton(onClick = { navigationActions.navigateTo(Screen.PROFILE) }) {
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "Profile Icon",
                    Modifier.size(32.dp).testTag("profileIcon"))
              }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = navigationItems,
            selectedItem = navigationActions.currentRoute(),
        )
      },
      content = { paddingValues ->
        currentProfile?.let { profile ->
          Spacer(modifier = Modifier.height(16.dp).testTag("spacer"))

          if (lessons.any {
            it.status == LessonStatus.CONFIRMED ||
                it.status == LessonStatus.REQUESTED ||
                it.status == LessonStatus.PENDING
          }) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .testTag("lessonsColumn")) {
                  // Expandable sections for Tutor
                  if (profile.role == Role.TUTOR) {
                    ExpandableLessonSection(
                        sectionTitle = "Pending Lessons",
                        lessons = lessons,
                        statusFilter = LessonStatus.PENDING,
                        isTutor = true,
                        maxHeight = 340.dp,
                        onClick = onLessonClick,
                        modifier = Modifier.testTag("pendingLessonsSection"))
                    Spacer(modifier = Modifier.height(16.dp))

                    ExpandableLessonSection(
                        sectionTitle = "Confirmed Lessons",
                        lessons = lessons,
                        statusFilter = LessonStatus.CONFIRMED,
                        isTutor = true,
                        onClick = onLessonClick,
                        modifier = Modifier.testTag("confirmedLessonsSection"))
                  }

                  // Expandable sections for Student
                  if (profile.role == Role.STUDENT) {
                    ExpandableLessonSection(
                        sectionTitle = "Requested Lessons",
                        lessons = lessons,
                        statusFilter = LessonStatus.REQUESTED,
                        isTutor = false,
                        maxHeight = 340.dp,
                        onClick = onLessonClick,
                        modifier = Modifier.testTag("requestedLessonsSection"))
                    Spacer(modifier = Modifier.height(16.dp))

                    ExpandableLessonSection(
                        sectionTitle = "Confirmed Lessons",
                        lessons = lessons,
                        statusFilter = LessonStatus.CONFIRMED,
                        isTutor = false,
                        onClick = onLessonClick,
                        modifier = Modifier.testTag("confirmedLessonsStudentSection"))
                  }
                }
          } else {
            Text(
                text = "You have no lessons scheduled at the moment.",
                modifier =
                    Modifier.padding(horizontal = 32.dp, vertical = 96.dp).testTag("noLessonsText"),
                style = MaterialTheme.typography.titleMedium)
          }
        } ?: NoProfileFoundScreen(context, navigationActions)
      })
}

@Composable
fun ExpandableLessonSection(
    sectionTitle: String,
    lessons: List<Lesson>,
    statusFilter: LessonStatus,
    isTutor: Boolean,
    maxHeight: Dp? = null, // Optional parameter to limit the section's height
    modifier: Modifier = Modifier,
    onClick: (Lesson) -> Unit,
) {
  var expanded by remember { mutableStateOf(true) }

  Column(modifier = modifier.fillMaxWidth()) {
    // Section header with click to expand/collapse
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp)
                .testTag("${sectionTitle}Row"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = sectionTitle,
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("${sectionTitle}Text"))
          Icon(
              imageVector =
                  if (expanded) Icons.Default.KeyboardArrowDown
                  else Icons.Default.KeyboardArrowLeft,
              contentDescription = if (expanded) "Collapse" else "Expand",
              modifier = Modifier.testTag("${sectionTitle}Icon"))
        }

    // Display lessons only when the section is expanded
    if (expanded) {
      Box(modifier = maxHeight?.let { Modifier.heightIn(max = it) } ?: Modifier.fillMaxWidth()) {
        DisplayLessons(
            lessons = lessons,
            statusFilter = statusFilter,
            isTutor = isTutor,
            onClick = onClick,
            modifier = Modifier.testTag("${sectionTitle}Lessons"))
      }
    }
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
