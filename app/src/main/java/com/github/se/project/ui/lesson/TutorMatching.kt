package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayTutors
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorMatchingScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
    val currentProfile =
        listProfilesViewModel.currentProfile.collectAsState().value
            ?: return Text("No profile selected. Should not happen.")

    val currentLesson =
        lessonViewModel.selectedLesson.collectAsState().value
            ?: return Text("No lesson selected. Should not happen.")

    var showFilterDialog by remember { mutableStateOf(false) }
    var showVerifiedOnly by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf(SortOption.PRICE) }

    val tutorProfilesFlow = remember {
        listProfilesViewModel.profiles.filter { profiles: List<Profile> ->
            profiles.any { profile -> profile.role == Role.TUTOR }
        }
    }
    val allTutorProfiles by tutorProfilesFlow.collectAsState(listOf())


    val filteredTutors =
        if (currentLesson.status == LessonStatus.MATCHING) {
            allTutorProfiles
                .asSequence()
                .filter { profile ->
                    profile.subjects.contains(currentLesson.subject) &&
                            profile.price >= currentLesson.minPrice &&
                            (!showVerifiedOnly || profile.certification?.verified == true) &&
                            isTutorAvailable(profile.schedule, currentLesson.timeSlot)
                }
                .toList()
                .let { tutors ->
                    when (sortBy) {
                        SortOption.PRICE -> tutors.sortedBy { it.price }
                        SortOption.ACADEMIC_LEVEL -> tutors.sortedByDescending { it.academicLevel.ordinal }
                        SortOption.VERIFICATION ->
                            tutors.sortedByDescending { it.certification?.verified == true }
                    }
                }
        } else {
            allTutorProfiles.filter { currentLesson.tutorUid.contains(it.uid) }.ifEmpty { return }
        }


    val context = LocalContext.current

    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topAppBar"),
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("backButton"),
                        onClick = { navigationActions.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back arrow"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Available Tutors",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("AvailableTutorsTitle")
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier.testTag("filterButton")
                    ) {
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Filter")
                    }
                })
        },
        bottomBar = {
            if (currentLesson.status == LessonStatus.MATCHING) {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("noTutorButton"),
                    onClick = {
                        lessonViewModel.addLesson(
                            currentLesson.copy(status = LessonStatus.STUDENT_REQUESTED),
                            onComplete = {
                                lessonViewModel.getLessonsForStudent(currentProfile.uid)
                                Toast.makeText(
                                    context,
                                    "Lesson sent successfully!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            })
                        navigationActions.navigateTo(Screen.HOME)
                    }) {
                    Text(
                        text = stringResource(id = R.string.request_another_tutor),
                        modifier = Modifier.testTag("noTutorButtonText"))
                }
            } else if (currentLesson.status == LessonStatus.STUDENT_REQUESTED) {
                // Cancellation Button
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("cancellationButton"),
                    colors =
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel the Lesson")
                }
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
          if (filteredTutors.isEmpty()) {
            Text(
                text =
                    "No tutor available for your lesson: go back to change your lesson or click on the button to wait for a tutor to choose your lesson.",
                modifier =
                    Modifier.align(Alignment.Center).padding(16.dp).testTag("noTutorMessage"),
                style = MaterialTheme.typography.bodyMedium)
          } else {
            DisplayTutors(
                modifier =
                    Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .testTag("tutorsList"),
                    tutors = filteredTutors,
                    onCardClick = { tutor ->
                        listProfilesViewModel.selectProfile(tutor)
                        navigationActions.navigateTo(Screen.SELECTED_TUTOR_DETAILS)
                    })
            }
        }

        // Cancellation Dialog
        if (showCancelDialog) {
            AlertDialog(
                modifier = Modifier.testTag("cancellationDialog"),
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(
                        text = "Lesson Cancellation",
                        modifier = Modifier.testTag("cancellationDialogTitle")
                    )
                },
                text = {
                    Text(
                        text =
                        "Are you sure you want to cancel the lesson? This action can not be undone.",
                        modifier = Modifier.testTag("cancellationDialogText")
                    )
                },
                confirmButton = {
                    Button(
                        modifier = Modifier.testTag("cancellationDialogConfirmButton"),
                        onClick = {
                            lessonViewModel.deleteLesson(
                                currentLesson.id,
                                onComplete = { lessonViewModel.getLessonsForStudent(currentProfile.uid) })
                            showCancelDialog = false
                            navigationActions.goBack()
                        }) {
                        Text("Yes, cancel it")
                    }
                },
                dismissButton = {
                    Button(
                        modifier = Modifier.testTag("cancellationDialogDismissButton"),
                        onClick = { showCancelDialog = false }) {
                        Text("No")
                    }
                })
        }

        if (showFilterDialog) {
            AlertDialog(
                modifier = Modifier.testTag("filterDialog"),
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filter Tutors") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Verified filter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Show verified tutors only")
                            Switch(
                                checked = showVerifiedOnly,
                                onCheckedChange = { showVerifiedOnly = it },
                                modifier = Modifier.testTag("verifiedSwitch")
                            )
                        }

                        // Sort options
                        Text("Sort by:", style = MaterialTheme.typography.titleSmall)
                        Column {
                            SortOption.entries.forEach { option ->
                                Row(
                                    modifier =
                                    Modifier.fillMaxWidth().clickable { sortBy = option }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = sortBy == option,
                                        onClick = { sortBy = option },
                                        modifier = Modifier.testTag("sortOption_${option.name}")
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(option.displayName)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFilterDialog = false },
                        modifier = Modifier.testTag("applyFiltersButton")
                    ) {
                        Text("Apply Filters")
                    }
                })

        }
    }
}

fun isTutorAvailable(tutorSchedule: List<List<Int>>, timeSlot: String): Boolean {
  if (tutorSchedule.size != 7 || tutorSchedule.any { it.size != 12 }) {
    throw IllegalArgumentException("Invalid schedule dimensions")
  }

  try {
    // Parse dd/MM/yyyyTHH:mm:ss format
    val parts = timeSlot.split("T")
    val dateParts = parts[0].split("/")
    val timeParts = parts[1].split(":")

    // Extract day and hour
    val date =
        java.time.LocalDate.of(dateParts[2].toInt(), dateParts[1].toInt(), dateParts[0].toInt())
    val hour = timeParts[0].toInt()

    val dayIndex = (date.dayOfWeek.value - 1) % 7
    val hourIndex = hour - 8

    if (hourIndex !in 0..11) {
      return false
    }

    return tutorSchedule[dayIndex][hourIndex] == 1
  } catch (e: Exception) {
    throw IllegalArgumentException("Invalid timeSlot format. Expected: dd/MM/yyyyTHH:mm:ss")
  }
}

enum class SortOption(val displayName: String) {
    PRICE("Price (lowest first)"),
    ACADEMIC_LEVEL("Academic Level (highest first)"),
    VERIFICATION("Verified tutors first")
}
