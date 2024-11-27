package com.github.se.project.ui.lesson

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.formatDate

@Composable
private fun LessonActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String,
    icon: @Composable () -> Unit,
    isError: Boolean = false
) {
    Button(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = if (isError) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        } else ButtonDefaults.buttonColors(),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .testTag(testTag)
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

private fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

private fun navigateWithToast(
    navigationActions: NavigationActions,
    context: Context,
    message: String,
    screen: String = Screen.HOME
) {
    context.showToast(message)
    navigationActions.navigateTo(screen)
}

@Composable
private fun LessonScreenTitle(status: LessonStatus) {
    val title = when (status) {
        LessonStatus.CONFIRMED -> "Confirmed Lesson"
        LessonStatus.PENDING_TUTOR_CONFIRMATION -> "Pending Tutor Confirmation"
        LessonStatus.INSTANT_CONFIRMED -> "Confirmed Instant Lesson"
        LessonStatus.STUDENT_REQUESTED -> "Pending Student Confirmation"
        else -> "Lesson Details"
    }
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedLessonScreen(
    listProfilesViewModel: ListProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
    val currentProfile = listProfilesViewModel.currentProfile.collectAsState().value
        ?: return Text("No profile found. Should not happen.")
    val isStudent = currentProfile.role == Role.STUDENT

    val lesson = lessonViewModel.selectedLesson.collectAsState().value
        ?: return Text("No lesson selected. Should not happen.")

    val otherProfile = if (isStudent) {
        listProfilesViewModel.getProfileById(lesson.tutorUid[0])
    } else {
        listProfilesViewModel.getProfileById(lesson.studentUid)
    } ?: return Text("Cannot retrieve profile")

    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navigationActions.goBack() },
                        modifier = Modifier.testTag("backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { LessonScreenTitle(lesson.status) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("confirmedLessonScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LessonDetailsCard(lesson, otherProfile)

            Spacer(modifier = Modifier.weight(1f))

            when {
                lesson.status in listOf(LessonStatus.CONFIRMED, LessonStatus.INSTANT_CONFIRMED) -> {
                    MessageButton(otherProfile, lesson, isStudent)
                }
                lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION && isStudent -> {
                    CancelLessonButton(lesson, currentProfile, lessonViewModel, navigationActions, context)
                }
                lesson.status == LessonStatus.STUDENT_REQUESTED && !isStudent -> {
                    CancelRequestButton(lesson, currentProfile, lessonViewModel, navigationActions, context)
                }
            }
        }
    }
}

@Composable
private fun LessonDetailsCard(lesson: Lesson, otherProfile: Profile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayLessonDetails(lesson, otherProfile)
            LessonLocationDisplay(
                latitude = lesson.latitude,
                longitude = lesson.longitude,
                lessonTitle = lesson.title
            )
        }
    }
}

@Composable
private fun MessageButton(otherProfile: Profile, lesson: Lesson, isStudent: Boolean) {
    val context = LocalContext.current
    LessonActionButton(
        text = "Message ${if (isStudent) "Tutor" else "Student"}",
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${otherProfile.phoneNumber}")
                putExtra(
                    "sms_body",
                    "Hello, about our lesson ${formatDate(lesson.timeSlot)}..."
                )
            }
            context.startActivity(intent)
        },
        testTag = "contactButton",
        icon = {
            Icon(
                Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
private fun CancelLessonButton(
    lesson: Lesson,
    currentProfile: Profile,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
    context: Context
) {
    LessonActionButton(
        text = "Cancel Lesson",
        onClick = {
            lessonViewModel.deleteLesson(
                lesson.id,
                onComplete = {
                    lessonViewModel.getLessonsForStudent(currentProfile.uid) {}
                    navigateWithToast(navigationActions, context, "Lesson cancelled successfully")
                }
            )
        },
        testTag = "cancelButton",
        icon = {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        isError = true
    )
}

@Composable
private fun CancelRequestButton(
    lesson: Lesson,
    currentProfile: Profile,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
    context: Context
) {
    LessonActionButton(
        text = "Cancel your request",
        onClick = {
            lessonViewModel.updateLesson(
                lesson.copy(
                    tutorUid = lesson.tutorUid.filter { it != currentProfile.uid }
                ),
                onComplete = {
                    lessonViewModel.getLessonsForTutor(currentProfile.uid) {}
                    navigateWithToast(navigationActions, context, "Request cancelled successfully")
                }
            )
        },
        testTag = "cancelButton",
        icon = {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        isError = true
    )
}