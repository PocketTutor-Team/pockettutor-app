package com.github.se.project.ui.lesson

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
) {

  val profile = listProfilesViewModel.currentProfile.collectAsState()

  val currentLesson = lessonViewModel.selectedLesson.collectAsState().value

  val onConfirm = { lesson: Lesson ->
    if (currentLesson == null) {
      lesson.id = lessonViewModel.getNewUid()
    }
    lessonViewModel.selectLesson(lesson)
    navigationActions.navigateTo(Screen.TUTOR_MATCH)
  }

  LessonEditor(
      mainTitle = "Schedule a lesson",
      profile = profile.value!!,
      lesson = currentLesson,
      onBack = { navigationActions.navigateTo(Screen.HOME) },
      onConfirm = onConfirm,
      onDelete = null,
      couldBeInstant = true)
}
