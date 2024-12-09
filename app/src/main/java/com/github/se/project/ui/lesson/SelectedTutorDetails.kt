package com.github.se.project.ui.lesson

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.ErrorState
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedTutorDetailsScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
  val tutorProfile =
      listProfilesViewModel.selectedProfile.collectAsState().value
          ?: return Text("No profile selected. Should not happen.")

  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text("No profile selected. Should not happen.")

  val currentLesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen.")

  val context = LocalContext.current
  var showConfirmDialog by remember { mutableStateOf(false) }

  val completedLessons by lessonViewModel.currentUserLessons.collectAsState()
  val ratedLessons =
      completedLessons.filter {
        it.tutorUid.contains(tutorProfile.uid) &&
            it.status == LessonStatus.COMPLETED &&
            it.rating != null
      }

  Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topBar"),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            title = {
              Text(
                  text = "Available Tutors",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("confirmLessonTitle"))
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("selectedTutorDetailsScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

              // Check if the given profile is a tutor
              if (tutorProfile.role != Role.TUTOR) {
                ErrorState(message = "No tutor selected. Should not happen.")
              } else {
                // Tutor information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                      DisplayTutorDetails(
                          tutorProfile = tutorProfile,
                          completedLessons = ratedLessons,
                          listProfilesViewModel = listProfilesViewModel)
                    }

                Spacer(modifier = Modifier.weight(1f))

                // Confirmation button
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showConfirmDialog = true },
                    modifier =
                        Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("confirmButton")) {
                      Icon(
                          Icons.AutoMirrored.Filled.Send,
                          contentDescription = "Confirmation Button Icon",
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Confirm your lesson")
                    }
              }
            }

        // Confirmation Dialog
        if (showConfirmDialog) {
          AlertDialog(
              modifier = Modifier.testTag("confirmDialog"),
              onDismissRequest = { showConfirmDialog = false },
              title = {
                Text(
                    text = "Confirm Your Choice", modifier = Modifier.testTag("confirmDialogTitle"))
              },
              text = {
                Text(
                    text =
                        "Would you like to choose ${tutorProfile.firstName} ${tutorProfile.lastName} for your lesson and pay a price of ${tutorProfile.price}.-/hour?",
                    modifier = Modifier.testTag("confirmDialogText"))
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("confirmDialogButton"),
                    onClick = {
                      lessonViewModel.addLesson(
                          currentLesson.copy(
                              tutorUid = listOf(tutorProfile.uid),
                              price = tutorProfile.price.toDouble(),
                              status =
                                  if (currentLesson.status == LessonStatus.STUDENT_REQUESTED)
                                      LessonStatus.CONFIRMED
                                  else LessonStatus.PENDING_TUTOR_CONFIRMATION,
                          ),
                          onComplete = {
                            lessonViewModel.getLessonsForStudent(currentProfile.uid)
                            Toast.makeText(context, "Lesson sent successfully!", Toast.LENGTH_SHORT)
                                .show()
                            navigationActions.navigateTo(Screen.HOME)
                          })
                    }) {
                      Text("Confirm")
                    }
              },
              dismissButton = {
                TextButton(
                    modifier = Modifier.testTag("confirmDialogCancelButton"),
                    onClick = { showConfirmDialog = false }) {
                      Text("Cancel")
                    }
              })
        }
      }
}

// Display the tutor details
@Composable
private fun DisplayTutorDetails(
    tutorProfile: Profile,
    completedLessons: List<Lesson>,
    listProfilesViewModel: ListProfilesViewModel
) {
  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    TutorInfoSection(tutorProfile, completedLessons)

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

    TutorDescriptionSection(tutorProfile.description)

    if (completedLessons.isNotEmpty()) {
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
      TutorReviewsSection(completedLessons, listProfilesViewModel)
    }
  }
}

@Composable
private fun TutorInfoSection(profile: Profile, completedLessons: List<Lesson>) {
  Row(
      modifier = Modifier.fillMaxWidth().testTag("tutorInfoRow"),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        DisplayProfilePicture("tutorProfilePicture", profile)

        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = "${profile.firstName} ${profile.lastName}",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("tutorName"))
          Text(
              text = "${profile.section} - ${profile.academicLevel}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("tutorAcademicInfo"))
        }

        Column(horizontalAlignment = Alignment.End) {
          val averageRating =
              if (completedLessons.isNotEmpty()) {
                completedLessons.mapNotNull { it.rating?.grade }.average()
              } else null

          if (averageRating != null) {
            DisplayRatingGrade(averageRating, "tutorRating")
          }

          Text(
              text = "${profile.price}.-/h",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.testTag("tutorPrice"))
        }
      }
}

@Composable
private fun TutorReviewsSection(
    completedLessons: List<Lesson>,
    listProfilesViewModel: ListProfilesViewModel
) {
  Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth().testTag("tutorReviewsSection")) {
        Text(
            text = "Recent Reviews",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("tutorReviewsTitle"))

        completedLessons
            .sortedByDescending { it.rating?.date }
            .take(3)
            .forEach { lesson ->
              val student = listProfilesViewModel.getProfileById(lesson.studentUid)
              if (student != null && lesson.rating != null) {
                DisplayReview(lesson, student)
              }
            }
      }
}

@Composable
private fun DisplayReview(lesson: Lesson, student: Profile) {
  val rating = lesson.rating ?: return

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
              .testTag("reviewCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                          text = "${student.firstName} ${student.lastName}",
                          style = MaterialTheme.typography.titleMedium)
                      Text(
                          text = "${lesson.subject.name} - ${formatDate(lesson.timeSlot)}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                          repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint =
                                    if (index < lesson.rating.grade)
                                        MaterialTheme.colorScheme.primary
                                    else Color.Transparent,
                                modifier = Modifier.size(16.dp))
                          }
                        }
                  }

              if (rating.comment.isNotEmpty()) {
                Text(
                    text = rating.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("reviewComment"))
              }
            }
      }
}

@Composable
private fun TutorDescriptionSection(description: String) {
  Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth().testTag("tutorDescriptionSection")) {
        if (description.isEmpty()) {
          Text(
              text = "No description available.",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.testTag("tutorDescriptionEmpty"))
        } else {
          Text(
              text = description,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.testTag("tutorDescription"))
        }
      }
}

@SuppressLint("DefaultLocale")
@Composable
private fun DisplayRatingGrade(grade: Double, testTag: String) {
  Row {
    Text(
        text = String.format("%.2f", grade),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag(testTag + "Label"))
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Rating",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp).padding(start = 4.dp).testTag(testTag + "Icon"))
  }
}

@Composable
private fun DisplayProfilePicture(testTag: String, tutorProfile: Profile) {
  Box(contentAlignment = Alignment.Center) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) {
          Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              modifier = Modifier.padding(8.dp).testTag(testTag),
              tint = MaterialTheme.colorScheme.primary)
        }

    if (tutorProfile.certification?.verified == true) {
      Surface(
          modifier = Modifier.size(24.dp).align(Alignment.BottomEnd).offset(x = 8.dp, y = 8.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.background,
          shadowElevation = 2.dp) {
            Surface(
                modifier = Modifier.padding(2.dp).fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                  Icon(
                      imageVector = ImageVector.vectorResource(id = R.drawable.epflpng),
                      contentDescription = "EPFL Verified",
                      modifier = Modifier.padding(2.dp),
                      tint = Color.Red)
                }
          }
    }
  }
}
