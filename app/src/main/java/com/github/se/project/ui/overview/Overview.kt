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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
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
import com.android.sample.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.TopLevelDestination

@Composable
fun homeScreen(
    listProfilViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
) {

    val context = LocalContext.current
    val currentProfile = listProfilViewModel.currentProfile.collectAsState().value
    val lessons = lessonViewModel.lessons.collectAsState()

    // Show a Toast message when no current profile is found but it should never happen
    //if (currentProfile == null) {
    //    Toast.makeText(context, "No profil is currently assigned to the current user", Toast.LENGTH_LONG).show()
    //    Log.e("homeScreen", "No profil is currently assigned to the current user")
    //}

    // Determine which bottom navigation items to show based on the user's role
    val tabList = when (currentProfile?.role) {
        Role.TUTOR -> listOf(
            TopLevelDestination(Route.HOME, Icons.Outlined.Email, "my_work_space_route"),
            TopLevelDestination(Route.HOME, Icons.Outlined.Search, "find_tutor_route")
        )
        else -> listOf(
            TopLevelDestination(Route.HOME, Icons.Outlined.Email, "my_work_space_route"),
            TopLevelDestination(Route.HOME, Icons.Outlined.Search, "find_student_route")
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = tabList,
                selectedItem = navigationActions.currentRoute()
            )
        },
        content = { paddingValues ->
            if (currentProfile == null) {
                // Display a message when no profile is found
                NoProfileFoundScreen(context, navigationActions)
            } else if (lessons.value.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(lessons.value.size) { index ->
                        if (currentProfile != null) {
                            LessonItem(currentProfile,lesson = lessons.value[index], onClick = {
                                // Logic for selecting a lesson and navigating to the edit screen
                                lessonViewModel.selectLesson(lessons.value[index])
                                //TODO: nagivate to edit lesson screen
                                //navigationActions.navigateTo(Route.EDIT_LESSON)
                            })
                        } else {
                            Toast.makeText(context, "No profil is currently assigned to the current user", Toast.LENGTH_LONG).show()
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
        }
    )
}

@Composable
fun LessonItem(currentProfile: Profile ,lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Lesson Title at the top
            Text(
                text = lesson.title
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Row to display Lesson.timeSlot, currentProfileFirstName, and Lesson.price on the same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lesson.timeSlot,
                )

                Text(
                    text = currentProfile?.firstName ?: "Unknown",
                )

                Text(
                    text = "Price: \$${lesson.price}"
                )
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No profile is currently assigned to the current user.")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navigationActions.navigateTo(Route.AUTH) }) {
            Text(text = "Go to the authentication screen")
        }

    }
}
