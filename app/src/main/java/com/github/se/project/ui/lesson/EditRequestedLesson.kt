package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun EditRequestedLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
) {

  val profile = listProfilesViewModel.currentProfile.collectAsState()
  val selectedLocation by lessonViewModel.selectedLocation.collectAsState()

  val currentLesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen")

  val context = LocalContext.current

  val onConfirm = { lesson: Lesson ->
    lessonViewModel.updateLesson(
        lesson,
        onComplete = {
          lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
          Toast.makeText(context, "Lesson updated successfully", Toast.LENGTH_SHORT).show()
        })

    navigationActions.navigateTo(Screen.HOME)
  }

  val onDelete = { lesson: Lesson ->
    lessonViewModel.deleteLesson(
        lesson.id,
        onComplete = {
          lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
          Toast.makeText(context, "Lesson deleted successfully", Toast.LENGTH_SHORT).show()
        })

    navigationActions.navigateTo(Screen.HOME)
  }

  LessonEditor(
      mainTitle = "Edit requested lesson",
      profile = profile.value!!,
      lesson = currentLesson,
      onBack = { navigationActions.navigateTo(Screen.HOME) },
      onConfirm = onConfirm,
      onDelete = onDelete)
}
