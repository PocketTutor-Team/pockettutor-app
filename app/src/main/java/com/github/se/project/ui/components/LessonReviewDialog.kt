package com.github.se.project.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.theme.SampleAppTheme

@Composable
fun LessonReviewDialog(
    lesson: Lesson,
    tutor: Profile? = null,
    initialRating: LessonRating? = null,
    onDismiss: () -> Unit,
    onSubmitReview: (rating: Int, comment: String) -> Unit
) {
  var rating by remember { mutableIntStateOf(initialRating?.grade ?: 0) }
  var comment by remember { mutableStateOf(initialRating?.comment ?: "") }
  val starColor = Color(0xFFFFC107) // Couleur jaune pour les étoiles

  Dialog(onDismissRequest = onDismiss) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
          Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Review Your Lesson",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)) {
                      Column(
                          modifier = Modifier.padding(vertical = 16.dp),
                          horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                lesson.subject.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            if (tutor != null) {
                              Text(
                                  "with ${tutor.firstName} ${tutor.lastName}",
                                  style = MaterialTheme.typography.titleMedium,
                                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                  modifier = Modifier.padding(top = 4.dp))
                            }
                          }
                    }

                Divider()

                Text("Rate your experience", style = MaterialTheme.typography.titleMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  repeat(5) { index ->
                    IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(48.dp)) {
                      Icon(
                          imageVector =
                              if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                          contentDescription = "Star ${index + 1}",
                          tint =
                              if (index < rating) starColor else MaterialTheme.colorScheme.outline,
                          modifier = Modifier.size(32.dp))
                    }
                  }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your feedback") },
                    placeholder = { Text("How was your experience?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                      OutlinedButton(
                          onClick = onDismiss,
                          modifier = Modifier.weight(1f),
                          shape = RoundedCornerShape(24.dp)) {
                            Text("Dismiss")
                          }
                      Button(
                          onClick = {
                            onSubmitReview(rating, comment)
                            onDismiss()
                          },
                          modifier = Modifier.weight(1f),
                          enabled = rating > 0,
                          shape = RoundedCornerShape(24.dp)) {
                            Text("Submit")
                          }
                    }
              }
        }
  }
}

@Preview
@Composable
fun LessonReviewDialogPreview() {
  SampleAppTheme {
    LessonReviewDialog(
        lesson =
            Lesson(
                id = "1",
                title = "Mathematics Tutoring",
                description = "Advanced calculus lesson",
                subject = Subject.ANALYSIS,
                latitude = 46.520374,
                longitude = 6.568339),
        initialRating = null,
        onDismiss = {},
        onSubmitReview = { _, _ -> },
        tutor =
            Profile(
                uid = "1",
                firstName = "John",
                lastName = "Doe",
                googleUid = "123",
                role = Role.TUTOR,
                academicLevel = AcademicLevel.BA5,
                section = Section.IN,
                languages = emptyList(),
                subjects = emptyList(),
                price = 16,
                phoneNumber = "1234567890"))
  }
}
