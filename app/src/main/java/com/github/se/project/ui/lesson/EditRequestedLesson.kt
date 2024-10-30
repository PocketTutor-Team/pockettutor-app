package com.github.se.project.ui.lesson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun EditRequestedLessonScreen(
    lesson: Lesson,
    navigationActions: NavigationActions,
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
    // Initialize states with current lesson values
    var title by remember { mutableStateOf(lesson.title) }
    var description by remember { mutableStateOf(lesson.description) }
    var selectedSubject by remember { mutableStateOf(lesson.subject) }
    val selectedLanguages = remember { mutableStateListOf<Language>().apply { addAll(lesson.languages) } }
    var minPrice by remember { mutableDoubleStateOf(lesson.minPrice) }
    var maxPrice by remember { mutableDoubleStateOf(lesson.maxPrice) }
    val calendar = Calendar.getInstance()

    // Format for date and time
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val context = LocalContext.current

    // Date and Time
    var selectedDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var selectedTime by remember { mutableStateOf(timeFormat.format(calendar.time)) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDate = dateFormat.format(selectedCalendar.time)
            calendar.time = selectedCalendar.time // Update calendar time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val selectedCalendar = Calendar.getInstance().apply {
                time = calendar.time // Use existing calendar time for date part
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }

            // Prevent selecting a past time
            if (selectedCalendar.timeInMillis < System.currentTimeMillis()) {
                Toast.makeText(context, "You cannot select a past time", Toast.LENGTH_SHORT).show()
            } else {
                selectedTime = timeFormat.format(selectedCalendar.time)
                calendar.time = selectedCalendar.time // Update calendar time
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Image upload placeholder function
    val onImageClick = {
        Toast.makeText(context, "Image upload not implemented", Toast.LENGTH_SHORT).show()
    }

    // Update Lesson Handler
    val onConfirmChanges = {
        val updatedLesson = lesson.copy(
            title = title,
            description = description,
            subject = selectedSubject,
            languages = selectedLanguages.toList(),
            minPrice = minPrice,
            maxPrice = maxPrice,
            timeSlot = "${selectedDate}T${selectedTime}:00", // Use updated calendar time
            status = LessonStatus.REQUESTED
        )
        lessonViewModel.updateLesson(updatedLesson, onSuccess = {
            Toast.makeText(context, "Lesson updated successfully", Toast.LENGTH_SHORT).show()
            navigationActions.navigateTo(Screen.HOME)
        }) {
            Toast.makeText(context, "Failed to update lesson", Toast.LENGTH_SHORT).show()
        }
    }

    // UI Layout
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit requested lesson",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { navigationActions.navigateTo(Screen.HOME) }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Edit the title") },
                    placeholder = { Text("Enter lesson title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Add a description") },
                    placeholder = { Text("Detailed lesson description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Image picker
                TextButton(onClick = onImageClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Click to add image(s)")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date and Time pickers
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(selectedDate.ifEmpty { "Select Date" })
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(selectedTime.ifEmpty { "Select Time" })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subject and Language selection
                SubjectSelector(subject = selectedSubject, onSubjectSelected = { selectedSubject = it })

                LanguageSelector(selectedLanguages = selectedLanguages)

                // Price range slider
                PriceRangeSlider("Modify the price range for your lesson:") { min, max ->
                    minPrice = min.toDouble()
                    maxPrice = max.toDouble()
                }

                Text("Selected price range: ${minPrice.toInt()} - ${maxPrice.toInt()}")
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Confirm changes button
                Button(
                    onClick = onConfirmChanges,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirm changes")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete lesson button
                Button(
                    onClick = {
                        lessonViewModel.deleteLesson(lesson.id) {
                            Toast.makeText(context, "Lesson deleted successfully", Toast.LENGTH_SHORT).show()
                            navigationActions.navigateTo(Screen.HOME)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete the lesson")
                }
            }
        }
    )
}
