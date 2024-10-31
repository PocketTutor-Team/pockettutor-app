package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {

  val profile = listProfilesViewModel.currentProfile.collectAsState()
  val selectedLocation by lessonViewModel.selectedLocation.collectAsState()

  val context = LocalContext.current

  val onConfirm = { lesson: Lesson ->
    lesson.id = lessonViewModel.getNewUid()
    lessonViewModel.addLesson(
        lesson,
        onComplete = {
          lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
          Toast.makeText(context, "Lesson added successfully", Toast.LENGTH_SHORT).show()
        })

    navigationActions.navigateTo(Screen.HOME)
  }

  LessonEditor(
      mainTitle = "Schedule a new lesson",
      profile = profile.value!!,
      selectedLocation = selectedLocation,
      lesson = null,
      onBack = { navigationActions.navigateTo(Screen.HOME) },
      onLocationPicker = { navigationActions.navigateTo(Screen.MAP_LOC_PICKER) },
      onConfirm = onConfirm,
      onDelete = null)
}
