package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.components.isInstant
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

/**
 * Composable function for the "Add Lesson" screen. Handles scheduling a new lesson and manages
 * navigation between screens.
 *
 * @param navigationActions Provides navigation actions for navigating between screens.
 * @param listProfilesViewModel ViewModel for accessing the current user's profile.
 * @param lessonViewModel ViewModel for managing lessons.
 * @param onMapReadyChange Callback for handling changes in the map's readiness state.
 */
@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    networkStatusViewModel: NetworkStatusViewModel,
    onMapReadyChange: (Boolean) -> Unit = {}
) {

  val profile = listProfilesViewModel.currentProfile.collectAsState()

    val isConnected = networkStatusViewModel.isConnected.collectAsState().value

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
      // For non-instant lessons, navigates to the tutor match screen
      lessonViewModel.selectLesson(lesson)
      navigationActions.navigateTo(Screen.TUTOR_MATCH)
    }
  }

  /**
   * Displays the LessonEditor component, which handles lesson input fields and interaction
   * callbacks.
   *
   * @param mainTitle The title displayed at the top of the editor.
   * @param profile The current user's profile.
   * @param lesson The lesson being edited or created.
   * @param onBack Callback for handling the "back" action.
   * @param onConfirm Callback for confirming the lesson input.
   * @param onDelete Unused in this context; passed as null.
   * @param onMapReady Callback for handling changes in the map's readiness state.
   */
  LessonEditor(
      mainTitle = "Schedule a lesson",
      profile = profile.value!!,
      lesson = currentLesson,
      isConnected = isConnected,
      onBack = { navigationActions.navigateTo(Screen.HOME) },
      onConfirm = onConfirm,
      onDelete = null,
      onMapReady = onMapReadyChange)
}
