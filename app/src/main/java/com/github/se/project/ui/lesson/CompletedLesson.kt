import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedLessonScreen(
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions
) {
  val lesson by lessonViewModel.selectedLesson.collectAsState()
  val currentUserProfile by listProfilesViewModel.currentProfile.collectAsState()

  if (lesson == null || currentUserProfile == null) {
    Text(
        text = "Error. Should not happen",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxSize().padding(16.dp),
        color = MaterialTheme.colorScheme.error)
  } else {
    val otherProfile =
        if (currentUserProfile!!.role == Role.STUDENT) {
          listProfilesViewModel.getProfileById(lesson!!.tutorUid.firstOrNull()!!)
        } else {
          listProfilesViewModel.getProfileById(lesson!!.studentUid)
        }

    Scaffold(
        topBar = {
          TopAppBar(
              title = { Text("Lesson Details", style = MaterialTheme.typography.titleLarge) },
              navigationIcon = {
                IconButton(onClick = { navigationActions.goBack() }) {
                  Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
              },
          )
        }) { paddingValues ->
          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .fillMaxSize()
                      .padding(horizontal = 16.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (otherProfile != null) {
                  // give a bit more space
                  Spacer(Modifier.padding(2.dp))

                  // Role Information
                  Text(
                      text =
                          "${if (otherProfile.role == Role.TUTOR) "Tutor" else "Student"} and lesson information",
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.primary,
                  )

                  // Lesson Details
                  DisplayLessonDetails(lesson = lesson!!, profile = otherProfile)

                  // give a bit more space
                  Spacer(Modifier.padding(8.dp))

                  // Review Header
                  Text(
                      text =
                          if (currentUserProfile!!.role == Role.TUTOR) "Student's Review"
                          else "Your Review",
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.primary)

                  // Review Card
                  val student = listProfilesViewModel.getProfileById(lesson!!.studentUid)
                  if (student != null) {
                    DisplayReview(lesson!!, student)
                  }
                } else {
                  Text(
                      text = "Profile not found.",
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.error,
                      modifier = Modifier.padding(16.dp))
                }
              }
        }
  }
}

@Composable
private fun DisplayReview(lesson: Lesson, student: Profile) {
  val rating = lesson.rating

  if (rating == null) {
    // Display a card for lessons that have not been rated yet
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium) {
          Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Lesson not rated yet!",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurface)
              }
        }
  } else {
    // Display the review if a rating exists
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium) {
          Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Review Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${student.firstName} ${student.lastName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = "${lesson.subject.name} - ${formatDate(lesson.timeSlot)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }

                      Row(
                          horizontalArrangement = Arrangement.spacedBy(2.dp),
                          verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                              Icon(
                                  imageVector = Icons.Filled.Star,
                                  contentDescription = "Star",
                                  tint =
                                      if (index < rating.grade) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.secondary,
                                  modifier = Modifier.size(16.dp))
                            }
                          }
                    }

                // Review Comment
                if (rating.comment.isNotEmpty()) {
                  Text(
                      text = rating.comment,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                      modifier = Modifier.padding(top = 4.dp))
                } else {
                  Text(
                      text = "Lesson not commented yet!",
                      style =
                          MaterialTheme.typography.bodyMedium.copy(
                              fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                      modifier = Modifier.padding(top = 4.dp))
                }
              }
        }
  }
}
