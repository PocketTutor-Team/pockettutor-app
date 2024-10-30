package com.github.se.project.ui.lesson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import java.util.*

@Composable
fun EditScheduledLessonScreen(
    lesson: Lesson,
    onLessonUpdated: () -> Unit,
    onCancel: () -> Unit,
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
    // States for lesson fields
    var title by remember { mutableStateOf(TextFieldValue(lesson.title)) }
    var description by remember { mutableStateOf(TextFieldValue(lesson.description)) }
    var selectedSubject by remember { mutableStateOf(lesson.subject) }
    var selectedLanguages by remember { mutableStateOf(lesson.languages) }
    var price by remember { mutableStateOf(TextFieldValue(lesson.price.toString())) }
    var timeSlot by remember { mutableStateOf(Calendar.getInstance().apply { time = Date(lesson.timeSlot) }) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
        timeSlot.set(Calendar.YEAR, year)
        timeSlot.set(Calendar.MONTH, month)
        timeSlot.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }, timeSlot.get(Calendar.YEAR), timeSlot.get(Calendar.MONTH), timeSlot.get(Calendar.DAY_OF_MONTH))

    val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
        timeSlot.set(Calendar.HOUR_OF_DAY, hourOfDay)
        timeSlot.set(Calendar.MINUTE, minute)
    }, timeSlot.get(Calendar.HOUR_OF_DAY), timeSlot.get(Calendar.MINUTE), true)

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Subject Dropdown
        Box {
            TextField(
                value = TextFieldValue(selectedSubject.name),
                onValueChange = {},
                label = { Text("Subject") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = true }
            )
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                Subject.values().forEach { subject ->
                    DropdownMenuItem(onClick = {
                        selectedSubject = subject
                        isDropdownExpanded = false
                    }) {
                        Text(text = subject.name)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { datePickerDialog.show() }) {
                Text("Select Date")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { timePickerDialog.show() }) {
                Text("Select Time")
            }
        }

        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { updateLesson(lesson, title, description, selectedSubject, selectedLanguages, price, timeSlot, lessonViewModel, onLessonUpdated) },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Save Changes")
        }

        Button(
            onClick = {
                lessonViewModel.deleteLesson(lesson.id) { onCancel() }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Cancel Lesson")
        }
    }
}

private fun updateLesson(
    lesson: Lesson,
    title: TextFieldValue,
    description: TextFieldValue,
    selectedSubject: Subject,
    selectedLanguages: List<Language>,
    price: TextFieldValue,
    timeSlot: Calendar,
    lessonViewModel: LessonViewModel,
    onLessonUpdated: () -> Unit
) {
    val updatedLesson = lesson.copy(
        title = title.text,
        description = description.text,
        subject = selectedSubject,
        languages = selectedLanguages,
        price = price.text.toDoubleOrNull() ?: 0.0,
        timeSlot = timeSlot.time.toString(),
        status = LessonStatus.CONFIRMED
    )
    lessonViewModel.updateLesson(updatedLesson, onSuccess = { onLessonUpdated() }) { e ->
        Log.e("EditScheduledLessonScreen", "Error updating lesson", e)
    }
}
