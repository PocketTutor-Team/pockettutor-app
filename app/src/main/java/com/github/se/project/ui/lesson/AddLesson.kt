package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.components.isInstant
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    onMapReadyChange: (Boolean) -> Unit = {}
) {

  val profile = listProfilesViewModel.currentProfile.collectAsState()

  val context = LocalContext.current

  val currentLesson = lessonViewModel.selectedLesson.collectAsState().value

  val lessons = lessonViewModel.currentUserLessons.collectAsState().value

  val onConfirm = { lesson: Lesson ->
    if (currentLesson == null) {
      lesson.id = lessonViewModel.getNewUid()
    }
    if (isInstant(lesson)) {
      if (lessons.any {
        it.status == LessonStatus.INSTANT_REQUESTED || it.status == LessonStatus.INSTANT_CONFIRMED
      }) {
        Toast.makeText(context, "You already have an instant lesson scheduled", Toast.LENGTH_SHORT)
            .show()
      } else
          lessonViewModel.addLesson(
              lesson,
              onComplete = {
                lessonViewModel.getLessonsForStudent(profile.value!!.uid)
                Toast.makeText(context, "Lesson sent successfully!", Toast.LENGTH_SHORT).show()
                lessonViewModel.selectLesson(lesson)
                navigationActions.navigateTo(Screen.HOME)
              })
    } else {
      lessonViewModel.selectLesson(lesson)
      navigationActions.navigateTo(Screen.TUTOR_MATCH)
    }
  }

  LessonEditor(
      mainTitle = "Schedule a lesson",
      profile = profile.value!!,
      lesson = currentLesson,
      onBack = { navigationActions.navigateTo(Screen.HOME) },
      onConfirm = onConfirm,
      onDelete = null,
      onMapReady = onMapReadyChange)
}
