import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.project.model.profile.Profile
import com.github.se.project.R // Make sure to adjust the package according to your resources
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.sample.model.lesson.LessonsViewModel
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.TopLevelDestination

@Composable
fun WorkingSpaceScreen(profile: Profile,
                       listProfilViewModel: ListProfilViewModel,
                       lessonViewModel: LessonsViewModel,
                       navigationActions: NavigationActions,
                       ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display the title at the top
        Text(
            text = "Working space",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )

        // Display the main icon (graduation cap and book)
        Icon(
            imageVector = Icons.Filled.ThumbUp,
            contentDescription = "Graduation Cap and Book Icon",
            modifier = Modifier.size(120.dp)
        )

        // Display the message below the icon
        Text(
            text = "You have no lessons scheduled at the moment",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
        )
    }

    // Determine which bottom navigation items to show based on the user's role
    val tabList = if (profile.role == Role.STUDENT) {
        listOf(
            TopLevelDestination(Route.HOME, Icons.Outlined.Email,"my_work_space_route"),
            TopLevelDestination(Route.HOME, Icons.Outlined.Search, "find_tutor_route")
        )
    } else {
        listOf(
            TopLevelDestination(Route.HOME, Icons.Outlined.Email, "my_work_space_route"),
            TopLevelDestination(Route.HOME, Icons.Outlined.Search, "find_student_route")
        )
    }

    // Bottom navigation menu integration
    BottomNavigationMenu(
        onTabSelect = { route -> navigationActions.navigateTo(route)},
        tabList = tabList,
        selectedItem = navigationActions.currentRoute()
    )
}
