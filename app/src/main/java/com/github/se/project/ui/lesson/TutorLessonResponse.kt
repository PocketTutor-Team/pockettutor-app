package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun TutorLessonResponseScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions,
) {
  val lesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen.")

  val sliderPrice = remember {
    mutableFloatStateOf(
        (lesson.maxPrice.toFloat() - lesson.minPrice.toFloat()) / 2.0f + lesson.minPrice.toFloat())
  }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        Row(
            modifier = Modifier.testTag("topBar").fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back arrow",
                    modifier = Modifier.size(32.dp).testTag("backButton"))
              }

              Text(
                  text = "Respond to the request",
                  style = MaterialTheme.typography.headlineMedium,
                  modifier = Modifier.testTag("TutorLessonResponseTitle"))
            }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .testTag("tutorLessonResponseScreen")) {
              val studentProfile = listProfilesViewModel.getProfileById(lesson.studentUid)
              if (studentProfile == null) {
                Toast.makeText(
                        context,
                        "Cannot retrieve student profile for the given lesson",
                        Toast.LENGTH_SHORT)
                    .show()
                Text("ERROR: Cannot retrieve student profile for the chosen lesson")
              } else {
                DisplayLessonDetails(lesson, studentProfile)
              }

              PriceSlider(sliderPrice)
            }
      },
      bottomBar = {
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
            onClick = {
              // Update the lesson price and status and tutorUid
              lessonViewModel.updateLesson(
                  lesson.copy(
                      tutorUid = listProfilesViewModel.currentProfile.value!!.uid,
                      price = sliderPrice.floatValue.toInt().toDouble(),
                      status = LessonStatus.TUTOR_REQUESTED),
                  onComplete = {
                    lessonViewModel.getLessonsForTutor(
                        listProfilesViewModel.currentProfile.value!!.uid)
                    navigationActions.navigateTo(Screen.HOME)
                  })
            }) {
              Text("Offer yourself for this lesson")
            }
      })
}
