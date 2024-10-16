import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonStatus
import com.android.sample.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_STUDENT
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_TUTOR
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun HomeScreen(
    listProfilViewModele: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
) {

  val context = LocalContext.current
  val currentProfile = listProfilViewModele.currentProfile.collectAsState().value
  // TODO: uncomment the following line once the add lessons feature is implemented
  // val lessons = lessonViewModel.lessons.collectAsState().value
  // TODO: remove the following line once the add lessons feature is implemented
  val lessons =
      listOf(
          Lesson(
              id = "1",
              title = "Math",
              description = "Math lesson",
              tutorUid = "",
              studentUid = "2",
              price = 20.0,
              timeSlot = "Monday 10:00 - 12:00",
              status = LessonStatus.REQUESTED, // Status of the lesson
              language = ""),
          Lesson(
              id = "2",
              title = "Algebra",
              description = "Math lesson",
              tutorUid = "",
              studentUid = "2",
              price = 15.0,
              timeSlot = "Tuesday 10:00 - 12:00",
              status = LessonStatus.REQUESTED, // Status of the lesson
              language = ""))

  // Determine which bottom navigation items to show based on the user's role
  val tabList =
      when (currentProfile?.role) {
        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
      }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = tabList,
            selectedItem = navigationActions.currentRoute())
      },
      content = { paddingValues ->
        if (currentProfile == null) {
          // Display a message when no profile is found
          NoProfileFoundScreen(context, navigationActions)
        } else if (lessons.isNotEmpty()) {
          LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(lessons.size) { index ->
              if (currentProfile != null) {
                LessonItem(
                    currentProfile,
                    lesson = lessons[index],
                    onClick = {
                      // Logic for selecting a lesson and navigating to the edit screen
                      lessonViewModel.selectLesson(lessons[index])
                      // TODO: nagivate to edit lesson screen
                      Toast.makeText(context, "Navigate to EDIT_LESSON screen", Toast.LENGTH_LONG)
                          .show()
                      // TODO: uncomment the following line once the add lessons feature is
                      // implemented
                      // navigationActions.navigateTo(Route.EDIT_LESSON)
                    })
              } else {
                Toast.makeText(
                        context,
                        "No profil is currently assigned to the current user",
                        Toast.LENGTH_LONG)
                    .show()
                Log.e("homeScreen", "No profil is currently assigned to the current user")
              }
            }
          }
        } else {
          Text(
              modifier = Modifier.padding(paddingValues),
              text = "You have no lessons scheduled at the moment.",
          )
        }
      })
}

@Composable
fun LessonItem(currentProfile: Profile, lesson: Lesson, onClick: () -> Unit) {
  Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
    Column(modifier = Modifier.padding(8.dp)) {
      // Lesson Title at the top
      Text(text = lesson.title)

      Spacer(modifier = Modifier.height(4.dp))

      // Row to display Lesson.timeSlot, currentProfileFirstName, and Lesson.price on the same line
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = currentProfile.academicLevel.toString())
        Text(text = "\$${lesson.price}")
        Text(text = lesson.timeSlot)
      }
    }
  }
}

@Composable
fun NoProfileFoundScreen(context: Context, navigationActions: NavigationActions) {
  // Display an error message when no profile is assigned as it should never happen
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "No profile is currently assigned to the current user.")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navigationActions.navigateTo(Screen.HOME) }) {
          Text(text = "Go back to HOME screen")
        }
      }
}