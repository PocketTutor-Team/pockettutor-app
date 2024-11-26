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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRating
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.theme.SampleAppTheme

@Composable
fun LessonReviewDialog(
    lesson: Lesson,
    initialRating: LessonRating? = null,
    onDismiss: () -> Unit,
    onSubmitReview: (rating: Int, comment: String) -> Unit
) {
  var rating by remember { mutableIntStateOf(initialRating?.grade ?: 0) }
  var comment by remember { mutableStateOf(initialRating?.comment ?: "") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
          Column(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                      Text(
                          if (initialRating != null) "Edit Rating" else "Rate Lesson",
                          style = MaterialTheme.typography.headlineSmall,
                          color = MaterialTheme.colorScheme.primary)
                      Text(
                          lesson.title,
                          style = MaterialTheme.typography.titleMedium,
                          color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                // Rating Stars
                Text("Rate your experience", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)) {
                      repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }, modifier = Modifier.size(48.dp)) {
                              Icon(
                                  imageVector =
                                      if (index < rating) Icons.Filled.Star
                                      else Icons.Outlined.Star,
                                  contentDescription = "Star ${index + 1}",
                                  tint =
                                      if (index < rating) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.outline,
                                  modifier = Modifier.size(32.dp))
                            }
                      }
                    }

                // Comment Field
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Share your feedback (optional)") },
                    placeholder = { Text("How was your experience?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5)

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                      OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                      }
                      Button(
                          onClick = {
                            onSubmitReview(rating, comment)
                            onDismiss()
                          },
                          modifier = Modifier.weight(1f),
                          enabled = rating > 0) {
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
        onSubmitReview = { _, _ -> })
  }
}
