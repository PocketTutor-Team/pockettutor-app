package com.github.se.project.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.authentification.AuthenticationViewModel
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.components.EpflVerificationDialog
import com.github.se.project.ui.components.ProfilePhoto
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.capitalizeFirstLetter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    authenticationViewModel: AuthenticationViewModel,
    certificationViewModel: CertificationViewModel
) {
  val profileState = listProfilesViewModel.currentProfile.collectAsState()
  val lessons = lessonViewModel.currentUserLessons.collectAsState()
  var showVerificationDialog by remember { mutableStateOf(false) }

  val completedLessons =
      lessons.value.filter {
        it.status == LessonStatus.COMPLETED || it.status == LessonStatus.PENDING_REVIEW
      }

  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("profileTopBar"),
            title = {},
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("closeButton")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
            },
            actions = {
              IconButton(
                  onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
                  modifier = Modifier.testTag("editProfileButton")) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                  }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
      }) { padding ->
        val profile = profileState.value
        if (profile == null) {
          // If profile is null, show error message
          Box(
              modifier = Modifier.fillMaxSize().padding(padding),
              contentAlignment = Alignment.Center) {
                Text(
                    text = "Error loading profile…",
                    modifier = Modifier.testTag("errorLoadingProfile"))
              }
        } else {
          val isTutor = profile.role == Role.TUTOR

          Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Scrollable content
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(bottom = 80.dp) // Leave space for the fixed button
                        .verticalScroll(rememberScrollState())) {
                  // Profile Info Card
                  ProfileCard(
                      profile = profile,
                      completedLessonsCount = completedLessons.size,
                      onVerificationClick = { showVerificationDialog = true },
                      completedLessons = completedLessons)

                  // Lessons Box
                  Card(
                      modifier =
                          Modifier.padding(horizontal = 16.dp)
                              .fillMaxWidth()
                              .defaultMinSize(minHeight = 200.dp)
                              .fillMaxHeight(),
                      colors =
                          CardDefaults.cardColors(
                              containerColor =
                                  MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                          Text(
                              text = "Completed Lessons",
                              style = MaterialTheme.typography.titleMedium)

                          Spacer(modifier = Modifier.height(8.dp))

                          if (completedLessons.isEmpty()) {
                            EmptyState(
                                text = "No completed lessons yet", icon = Icons.Default.CheckCircle)
                          } else {
                            DisplayLessons(
                                lessons = completedLessons,
                                listProfilesViewModel = listProfilesViewModel,
                                isTutor = isTutor,
                                onCardClick = { lesson ->
                                  lessonViewModel.selectLesson(lesson)
                                  navigationActions.navigateTo(Screen.COMPLETED_LESSON)
                                })
                          }
                        }
                      }
                }

            // Fixed Sign Out Button at the bottom
            Button(
                onClick = {
                  authenticationViewModel.signOut {
                    listProfilesViewModel.setCurrentProfile(null)
                    navigationActions.navigateTo(Screen.AUTH)
                  }
                },
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer)) {
                  Text("Sign Out")
                }
          }

          // Verification Dialog
          if (showVerificationDialog && isTutor) {
            EpflVerificationDialog(
                isVerified = profile.certification?.verified == true,
                onDismiss = { showVerificationDialog = false },
                onVerify = { sciper -> certificationViewModel.verifySciperNumber(sciper) })
          }
        }
      }
}

private fun getMostFrequentSubject(lessons: List<Lesson>): String? {
  return lessons.groupBy { it.subject }.maxByOrNull { (_, lessons) -> lessons.size }?.key?.name
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileCard(
    profile: Profile,
    completedLessonsCount: Int,
    onVerificationClick: () -> Unit,
    completedLessons: List<Lesson> = emptyList()
) {
  Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Box {
                      ProfilePhoto(photoUri = profile.profilePhotoUrl, size = 80.dp)

                      if (profile.role == Role.TUTOR) {
                        Surface(
                            modifier =
                                Modifier.align(Alignment.BottomEnd)
                                    .offset(x = 8.dp, y = 8.dp)
                                    .clickable(onClick = onVerificationClick),
                            shape = CircleShape,
                            color =
                                if (profile.certification?.verified == true) Color.White
                                else MaterialTheme.colorScheme.primary) {
                              Icon(
                                  if (profile.certification?.verified == true)
                                      ImageVector.vectorResource(R.drawable.epflpng)
                                  else Icons.Default.Warning,
                                  contentDescription = "Verification status",
                                  modifier = Modifier.padding(4.dp).size(20.dp),
                                  tint =
                                      if (profile.certification?.verified == true) Color.Red
                                      else MaterialTheme.colorScheme.onPrimary)
                            }
                      }
                    }

                    // Profile Info
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                      Text(
                          text =
                              "${profile.firstName.capitalizeFirstLetter()} ${profile.lastName.capitalizeFirstLetter()}",
                          style = MaterialTheme.typography.headlineSmall,
                          modifier = Modifier.testTag("profileName"),
                      )
                      Text(
                          text = "${profile.section} - ${profile.academicLevel}",
                          style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                          modifier = Modifier.testTag("profileAcademicInfo"))
                      Text(
                          text = profile.role.name.lowercase().capitalizeFirstLetter(),
                          style = MaterialTheme.typography.labelMedium,
                          color = MaterialTheme.colorScheme.primary)
                    }
                  }

              // Stats
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem(
                        count = completedLessonsCount,
                        label =
                            if (profile.role == Role.TUTOR) "Lessons Given" else "Lessons Taken",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.testTag("lessonsCount"))

                    if (profile.role == Role.TUTOR) {
                      StatItem(
                          count = profile.price,
                          label = "Price/Hour",
                          icon = ImageVector.vectorResource(R.drawable.cash),
                          prefix = "CHF",
                          modifier = Modifier.testTag("priceText"))

                      val averageRating =
                          if (completedLessons.isNotEmpty()) {
                            completedLessons.mapNotNull { it.rating?.grade }.average()
                          } else null

                      if (averageRating != null) {
                        StatItem(
                            rating = averageRating, label = "Rating", icon = Icons.Default.Star)
                      }
                    } else {
                      getMostFrequentSubject(completedLessons)?.let { subject ->
                        StatItem(
                            text = subject.lowercase().capitalizeFirstLetter(),
                            label = "Most Studied",
                            icon = ImageVector.vectorResource(id = R.drawable.baseline_school_24))
                      }
                    }
                  }

              if (profile.role == Role.TUTOR) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant)

                // Languages Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          imageVector =
                              ImageVector.vectorResource(id = R.drawable.baseline_language_24),
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                          modifier = Modifier.size(24.dp))
                      Row(
                          modifier = Modifier.weight(1f).padding(start = 16.dp),
                          horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.languages.forEach { language ->
                              Surface(
                                  color = MaterialTheme.colorScheme.primaryContainer,
                                  shape = MaterialTheme.shapes.small) {
                                    Text(
                                        text = language.name.lowercase().capitalizeFirstLetter(),
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier =
                                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                                  }
                            }
                          }
                    }

                // Subjects Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          imageVector =
                              ImageVector.vectorResource(id = R.drawable.baseline_school_24),
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                          modifier = Modifier.size(24.dp))
                      FlowRow(
                          modifier = Modifier.weight(1f).padding(start = 16.dp),
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          verticalArrangement = Arrangement.spacedBy(8.dp),
                          maxItemsInEachRow = 3) {
                            profile.subjects.forEach { subject ->
                              Surface(
                                  color = MaterialTheme.colorScheme.secondaryContainer,
                                  shape = MaterialTheme.shapes.small) {
                                    Text(
                                        text =
                                            subject.name
                                                .lowercase()
                                                .capitalizeFirstLetter()
                                                .replace("_", " "),
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier =
                                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onPrimary)
                                  }
                            }
                          }
                    }
              }
            }
      }
}

@SuppressLint("DefaultLocale")
@Composable
private fun StatItem(
    count: Int = -1,
    rating: Double = -1.0,
    text: String = "",
    label: String,
    icon: ImageVector,
    prefix: String = "",
    modifier: Modifier = Modifier
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp))
        if (count != -1) {
          Text(
              text = if (prefix.isEmpty()) "$count" else "$prefix $count",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = modifier)
        } else if (rating != -1.0) {
          Text(
              text = if (prefix.isEmpty()) String.format("%.2f", rating) else "",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface)
        } else {
          Text(
              text = text,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
}

@Composable
private fun EmptyState(text: String, icon: ImageVector) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp))
      }
}
