package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonsViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonsViewModel =
        viewModel(factory = LessonsViewModel.Factory(listProfilesViewModel))
) {
  val profileState = listProfilesViewModel.currentProfile.collectAsState()
  val lessons = lessonViewModel.lessons.collectAsState()

  // Filter only lessons that are marked as COMPLETED
  val completedLessons = lessons.value.filter { it.status == LessonStatus.COMPLETED }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Profile Info") },
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            },
            actions = {
              IconButton(onClick = { /* Handle edit navigation to the EDIT_TODO_SCREEN */}) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
              }
            })
      }) { paddingValues ->
        // must check that the profile is not null
        profileState.value?.let { userProfile ->
          // Pass the profile and completedLessons into ProfileDetailsScreen
          ProfileDetailsScreen(
              profile = userProfile,
              completedLessons = completedLessons,
              modifier = Modifier.padding(paddingValues))
        }
            ?: run {
              // Display message if profile is null
              Text(text = "Error loading profile...")
            }
      }
}

/**
 * Displays the details of the user's profile, including their role and personal information.
 *
 * @param profile The current user's profile.
 * @param completedLessons The list of completed lessons to display.
 * @param modifier Optional modifier for the screen layout.
 */
@Composable
fun ProfileDetailsScreen(
    profile: Profile,
    completedLessons: List<Lesson>,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(16.dp)) {
    val isTutor = profile.role == Role.TUTOR

    // Display shared profile info
    Text(text = "${profile.firstName} ${profile.lastName}")
    Text(text = "${profile.academicLevel} ${if (isTutor) "Tutor" else "Student"}")
    Text(text = "${profile.section}")

    Spacer(modifier = Modifier.height(16.dp))

    // Display role-specific information
    if (isTutor) {
      Text(text = "${profile.price}.- per hour", style = MaterialTheme.typography.bodyLarge)
    }
    DisplayLessons(completedLessons = completedLessons, isTutor = isTutor)
  }
}

/**
 * Displays a list of completed lessons in a lazy scrollable column.
 *
 * @param completedLessons The list of completed lessons to display.
 * @param isTutor Whether the user viewing this screen is a tutor or a student.
 */
@Composable
fun DisplayLessons(completedLessons: List<Lesson>, isTutor: Boolean) {
  // display number of lessons
  Text(text = "${completedLessons.size} ${if (isTutor) "lessons given" else "lessons taken"}")

  // display the completed lessons in a LazyColumn
  LazyColumn {
    itemsIndexed(completedLessons) { _, lesson ->
      if (isTutor) {
        Text(text = "${lesson.title} with ${lesson.studentUid}")
      } else {
        Text(text = "${lesson.title} with ${lesson.tutorUid}")
      }
    }
  }
}
