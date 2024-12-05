package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
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
    onMapReadyChange: (Boolean) -> Unit = {}
) {
  // Retrieves the current user's profile from the ViewModel as a state object
  val profile = listProfilesViewModel.currentProfile.collectAsState()

  // Provides access to the current context (e.g., for showing Toast messages)
  val context = LocalContext.current

  // Observes the currently selected lesson from the ViewModel
  val currentLesson = lessonViewModel.selectedLesson.collectAsState().value

  // Retrieves the list of lessons associated with the current user
  val lessons = lessonViewModel.currentUserLessons.collectAsState().value

  /**
   * Lambda function to handle lesson confirmation. Either schedules a new lesson or updates the
   * selected lesson, depending on the user's input.
   *
   * @param lesson The lesson to be added or updated.
   */
  val onConfirm = { lesson: Lesson ->
    // Assigns a new unique ID to the lesson if it's a new lesson
    if (currentLesson == null) {
      lesson.id = lessonViewModel.getNewUid()
    }

    // Checks if the lesson is an "instant lesson" (based on its status)
    if (isInstant(lesson)) {
      // Prevents scheduling multiple instant lessons at the same time
      if (lessons.any {
        it.status == LessonStatus.INSTANT_REQUESTED || it.status == LessonStatus.INSTANT_CONFIRMED
      }) {
        // Displays a message if the user already has an instant lesson scheduled
        Toast.makeText(context, context.getString(R.string.already_instant), Toast.LENGTH_SHORT)
            .show()
      } else {
        // Adds the instant lesson and fetches updated lessons for the user
        lessonViewModel.addLesson(
            lesson,
            onComplete = {
              // Refreshes the lesson list for the user after successfully adding the lesson
              lessonViewModel.getLessonsForStudent(profile.value!!.uid)
              Toast.makeText(context, context.getString(R.string.lesson_created), Toast.LENGTH_SHORT).show()

              // Marks the new lesson as selected
              lessonViewModel.selectLesson(lesson)

              // Navigates back to the home screen
              navigationActions.navigateTo(Screen.HOME)
            })
      }
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
      mainTitle = "Schedule a lesson", // Title displayed at the top
      profile = profile.value!!, // Passes the user's profile to the editor
      lesson = currentLesson, // The current lesson being edited
      onBack = { navigationActions.navigateTo(Screen.HOME) }, // Navigates back to home on cancel
      onConfirm = onConfirm, // Handles lesson confirmation
      onDelete = null, // Lesson deletion is not handled in this screen
      onMapReady = onMapReadyChange // Updates the map's readiness state
      )
}
