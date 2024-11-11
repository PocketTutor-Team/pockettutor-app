package com.github.se.project.ui.lesson

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions

@Composable
fun ConfirmedLessonScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions,
    isStudent: Boolean
) {

  val lesson = lessonViewModel.selectedLesson.collectAsState().value!!
  val tutorProfile = listProfilesViewModel.getProfileById(lesson.tutorUid[0])!!
    val studenProfile = listProfilesViewModel.getProfileById(lesson.studentUid)!!

  Scaffold(
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "Scheduled lesson",
                  style = MaterialTheme.typography.headlineMedium,
                  textAlign = TextAlign.Center)
              IconButton(onClick = { navigationActions.goBack() }) { // Use goBack action here
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
              }
            }
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Use DisplayLessonDetails to show lesson and tutor details

            if (isStudent) {
                DisplayLessonDetails(
                    lesson = lesson, studentProfile = tutorProfile // Passing the tutor's profile
                )
            }   else {
                DisplayLessonDetails(
                    lesson = lesson, studentProfile = studenProfile // Passing the tutor's profile
                )
            }
            LessonLocationDisplay(lesson.latitude, lesson.longitude, lesson.title)
        }
      },
      bottomBar = {
        Button(
            onClick = { navigationActions.goBack() }, // Use goBack action here as well
            modifier = Modifier.fillMaxWidth().padding(16.dp)) {
              Text("Back")
            }
      })
}
