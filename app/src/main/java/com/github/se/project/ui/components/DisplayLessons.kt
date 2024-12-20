package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.SuitabilityScoreCalculator
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.utils.capitalizeFirstLetter
import com.github.se.project.utils.formatDate

object LessonColors {
  private val LightCompleted = Color(0xFFF3E5F5) // Violet pastel clair
  private val LightConfirmed = Color(0xFFE3F2FD) // Bleu pastel clair
  private val LightPending = Color(0xFFFFF3E0) // Orange pastel clair
  private val LightUrgent = Color(0xFFF3E5F5) // Violet pastel clair
  private val LightCancelled = Color(0xFFFFEBEE) // Rouge pastel clair
  private val LightInstantConfirmed = Color(0xFFBBDEFB) // Bleu ciel pastel

  private val DarkCompleted = Color(0xFF571E98) // Violet foncé
  private val DarkConfirmed = Color(0xFF1565C0) // Bleu foncé
  private val DarkPending = Color(0xFFD87F00) // Orange foncé
  private val DarkUrgent = Color(0xFF571E98) // Violet foncé
  private val DarkCancelled = Color(0xFF8E0000) // Rouge foncé
  private val DarkInstantConfirmed = Color(0xFF2196F3) // Bleu électrique
  private val DarkInstantRequested = Color(0xFF4CAF50) // Vert dynamique

  @Composable
  fun getLessonColor(
      status: LessonStatus,
      hasTutor: Boolean = false,
      isTutor: Boolean = false,
      tutorProposed: Boolean = false
  ): Color {
    val isDarkTheme = isSystemInDarkTheme()

    return when {
      tutorProposed -> if (isDarkTheme) DarkUrgent else LightUrgent
      status == LessonStatus.COMPLETED || status == LessonStatus.PENDING_REVIEW ->
          if (isDarkTheme) DarkCompleted else LightCompleted
      status == LessonStatus.CONFIRMED -> if (isDarkTheme) DarkConfirmed else LightConfirmed
      status == LessonStatus.PENDING_TUTOR_CONFIRMATION ->
          if (isDarkTheme) {
            if (isTutor) DarkUrgent else DarkPending
          } else {
            if (isTutor) LightUrgent else LightPending
          }
      status == LessonStatus.STUDENT_REQUESTED && hasTutor ->
          if (isDarkTheme) {
            if (!isTutor) DarkUrgent else DarkPending
          } else {
            if (!isTutor) LightUrgent else LightPending
          }
      status == LessonStatus.STUDENT_REQUESTED ->
          if (isDarkTheme) {
            if (!isTutor) DarkPending else DarkUrgent
          } else {
            if (!isTutor) LightPending else LightUrgent
          }
      status == LessonStatus.INSTANT_REQUESTED ->
          if (isDarkTheme) {
            if (!isTutor) DarkInstantRequested else DarkPending
          } else {
            if (!isTutor) LightInstantConfirmed else LightPending
          }
      status == LessonStatus.INSTANT_CONFIRMED ->
          if (isDarkTheme) DarkInstantConfirmed else LightInstantConfirmed
      status == LessonStatus.STUDENT_CANCELLED -> if (isDarkTheme) DarkCancelled else LightCancelled
      status == LessonStatus.TUTOR_CANCELLED -> if (isDarkTheme) DarkCancelled else LightCancelled
      else -> MaterialTheme.colorScheme.surface
    }
  }
}

@Composable
fun DisplayLessons(
    modifier: Modifier = Modifier,
    lessons: List<Lesson>,
    statusFilter: LessonStatus? = null,
    isTutor: Boolean,
    tutorEmpty: Boolean = false,
    onCardClick: (Lesson) -> Unit = {},
    listProfilesViewModel: ListProfilesViewModel,
    requestedScreen: Boolean = false,
    suitabilityScore: Int? = null,
    distance: Int? = null
) {
  val filteredLessons =
      statusFilter?.let {
        lessons.filter { lesson -> lesson.status == it && lesson.tutorUid.isEmpty() == tutorEmpty }
      } ?: lessons

  LaunchedEffect(Unit) { listProfilesViewModel.getProfiles() }

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    filteredLessons.forEachIndexed { index, lesson ->
      val otherUserProfile =
          if (isTutor) {
            listProfilesViewModel.profiles.value.find { it.uid == lesson.studentUid }
          } else {
            listProfilesViewModel.profiles.value.find { it.uid == lesson.tutorUid.firstOrNull() }
          }

      Card(
          modifier =
              Modifier.fillMaxWidth().testTag("lessonCard_$index").clickable {
                onCardClick(lesson)
              },
          colors =
              CardDefaults.cardColors(
                  containerColor =
                      LessonColors.getLessonColor(
                          status = lesson.status,
                          hasTutor = lesson.tutorUid.isNotEmpty(),
                          isTutor = isTutor,
                          tutorProposed = requestedScreen),
                  contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("lessonContent_$index"),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.Top) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                              Row {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.testTag("lessonTitle_$index"))
                              }

                              Text(
                                  text = formatDate(lesson.timeSlot),
                                  style = MaterialTheme.typography.bodyMedium,
                                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                                  modifier = Modifier.testTag("lessonDate_$index"))

                              if (!isInstant(lesson)) {
                                suitabilityScore?.let {
                                  Text(
                                      text = "Recommended at $it%",
                                      style =
                                          MaterialTheme.typography.bodyMedium.copy(
                                              fontWeight = FontWeight.Bold, // Make the text bold
                                              color =
                                                  SuitabilityScoreCalculator.getColorForScore(
                                                      it, isSystemInDarkTheme())))
                                }
                              } else {
                                distance?.let {
                                  Text(
                                      text = "Distance : $it m",
                                      style =
                                          MaterialTheme.typography.bodyMedium.copy(
                                              fontWeight = FontWeight.Bold,
                                              color =
                                                  getColorForDistance(it, isSystemInDarkTheme())))
                                }
                              }
                            }

                        Surface(
                            modifier = Modifier.padding(start = 8.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                            tonalElevation = 2.dp) {
                              Text(
                                  text =
                                      lesson.subject.name
                                          .lowercase()
                                          .capitalizeFirstLetter()
                                          .replace("_", " "),
                                  style = MaterialTheme.typography.labelMedium,
                                  modifier =
                                      Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                          .testTag("lessonSubject_$index"),
                                  color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                      }

                  if (lesson.status == LessonStatus.COMPLETED ||
                      lesson.status == LessonStatus.CONFIRMED ||
                      lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION ||
                      lesson.status == LessonStatus.PENDING_REVIEW ||
                      lesson.status == LessonStatus.INSTANT_CONFIRMED) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically) {
                          Icon(
                              imageVector = Icons.Default.Person,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.onSurfaceVariant,
                              modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(8.dp))
                          Text(
                              text =
                                  if (isTutor)
                                      "Student: ${otherUserProfile?.firstName ?: "Unknown"} ${otherUserProfile?.lastName ?: ""}"
                                  else
                                      "Tutor: ${otherUserProfile?.firstName ?: "Unknown"} ${otherUserProfile?.lastName ?: ""}",
                              style = MaterialTheme.typography.titleSmall,
                              color = MaterialTheme.colorScheme.onSurfaceVariant,
                              modifier = Modifier.testTag("lessonParticipant_$index"))
                        }
                  }
                }
          }
    }
  }
}

fun getColorForDistance(distanceInMeters: Int, isDarkTheme: Boolean): Color {
  val green = if (isDarkTheme) Color(0xFF00E676) else Color(0xFF2E7D32)
  val yellow = if (isDarkTheme) Color(0xFFFFD740) else Color(0xFFFBC02D)
  val red = if (isDarkTheme) Color(0xFFFF5252) else Color(0xFFD32F2F)

  return when {
    distanceInMeters <= 500 -> green
    distanceInMeters <= 3000 -> {
      // Interpolate between green and yellow for distances between 500m and 5km
      val progress = (distanceInMeters - 500f) / 2500f
      Color(
          red = lerp(green.red, yellow.red, progress),
          green = lerp(green.green, yellow.green, progress),
          blue = lerp(green.blue, yellow.blue, progress),
          alpha = 1f)
    }
    else -> {
      // Interpolate between yellow and red for distances beyond 5km
      val progress = ((distanceInMeters - 3000f) / 3000f).coerceAtMost(1f)
      Color(
          red = lerp(yellow.red, red.red, progress),
          green = lerp(yellow.green, red.green, progress),
          blue = lerp(yellow.blue, red.blue, progress),
          alpha = 1f)
    }
  }
}
