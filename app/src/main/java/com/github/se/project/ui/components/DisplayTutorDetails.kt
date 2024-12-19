package com.github.se.project.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.utils.formatDate

// Display the tutor details
@Composable
fun DisplayTutorDetails(
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
                                    else Color.Gray,
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
    ProfilePhoto(
        photoUri = tutorProfile.profilePhotoUrl,
        size = 60.dp,
        showPlaceholder = true,
        modifier = Modifier.size(60.dp).testTag(testTag))

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
