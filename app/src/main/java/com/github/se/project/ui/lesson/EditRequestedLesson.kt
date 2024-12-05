package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.LessonEditor
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

/**
 * Composable function for the "Edit Requested Lesson" screen. Allows users to edit or delete an
 * existing lesson that is in the "requested" state.
 *
 * @param navigationActions Provides navigation actions for navigating between screens.
 * @param listProfilesViewModel ViewModel for accessing the current user's profile.
 * @param lessonViewModel ViewModel for managing lessons.
 * @param onMapReadyChange Callback for handling changes in the map's readiness state.
 */
@Composable
fun EditRequestedLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    onMapReadyChange: (Boolean) -> Unit
) {
  // Retrieves the current user's profile from the ViewModel as a state object
  val profile = listProfilesViewModel.currentProfile.collectAsState()

  // Retrieves the currently selected lesson from the ViewModel.
  // If no lesson is selected, displays a fallback message.
  val currentLesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen")

  // Provides access to the current context (e.g., for showing Toast messages)
  val context = LocalContext.current

  /**
   * Lambda function to handle lesson updates. Updates the lesson details and navigates back to the
   * home screen upon completion.
   *
   * @param lesson The updated lesson to be saved.
   */
  val onConfirm = { lesson: Lesson ->
    // Updates the lesson in the ViewModel and refreshes the user's lesson list
    lessonViewModel.updateLesson(
        lesson,
        onComplete = {
          lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
          Toast.makeText(context, "Lesson updated successfully", Toast.LENGTH_SHORT).show()
        })

    // Navigates back to the home screen
    navigationActions.navigateTo(Screen.HOME)
  }

  /**
   * Lambda function to handle lesson deletion. Deletes the selected lesson and navigates back to
   * the home screen upon completion.
   *
   * @param lesson The lesson to be deleted.
   */
  val onDelete = { lesson: Lesson ->
    // Deletes the lesson from the ViewModel and refreshes the user's lesson list
    lessonViewModel.deleteLesson(
        lesson.id,
        onComplete = {
          lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
          Toast.makeText(context, "Lesson deleted successfully", Toast.LENGTH_SHORT).show()
        })

    // Navigates back to the home screen
    navigationActions.navigateTo(Screen.HOME)
  }

  /**
   * Displays the LessonEditor component, which allows users to edit lesson details or delete the
   * lesson.
   *
   * @param mainTitle Displays a title based on the lesson's status (instant or regular lesson).
   * @param profile The current user's profile.
   * @param lesson The lesson being edited.
   * @param onBack Callback for navigating back to the home screen.
   * @param onConfirm Callback for confirming lesson changes.
   * @param onDelete Callback for deleting the lesson.
   * @param onMapReady Callback for handling changes in the map's readiness state.
   */
  LessonEditor(
      mainTitle =
          if (currentLesson.status == LessonStatus.INSTANT_REQUESTED)
              "Edit requested instant lesson"
          else "Edit requested lesson", // Determines the title based on the lesson's status
      profile = profile.value!!, // Passes the user's profile to the editor
      lesson = currentLesson, // The current lesson being edited
      onBack = { navigationActions.navigateTo(Screen.HOME) }, // Navigates back to home on cancel
      onConfirm = onConfirm, // Handles lesson confirmation
      onDelete = onDelete, // Handles lesson deletion
      onMapReady = onMapReadyChange // Updates the map's readiness state
      )
}
