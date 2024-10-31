package com.github.se.project.ui.lesson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  val selectedLanguages = remember { mutableStateListOf<Language>() }
  val selectedSubject = remember { mutableStateOf(Subject.NONE) }
  var minPrice by remember { mutableDoubleStateOf(5.0) }
  var maxPrice by remember { mutableDoubleStateOf(50.0) }
  val calendar = Calendar.getInstance()
  val currentDateTime = Calendar.getInstance()

  val profile = listProfilesViewModel.currentProfile.collectAsState()
  val context = LocalContext.current

  var selectedDate by remember { mutableStateOf("") }
  var selectedTime by remember { mutableStateOf("") }

  // Date and Time Pickers setup
  val datePickerDialog =
      DatePickerDialog(
          context,
          { _, year, month, dayOfMonth -> selectedDate = "$dayOfMonth/${month + 1}/$year" },
          calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DAY_OF_MONTH))
  datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis

  val timePickerDialog =
      TimePickerDialog(
          context,
          { _, hourOfDay, minute -> selectedTime = String.format("%02d:%02d", hourOfDay, minute) },
          calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE),
          true)

  // Confirm action handler
  val onConfirm = {
    val error =
        validateLessonInput(
            title, description, selectedSubject, selectedLanguages, selectedDate, selectedTime)
    if (error != null) {
      Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    } else {
      lessonViewModel.addLesson(
          Lesson(
              lessonViewModel.getNewUid(),
              title,
              description,
              selectedSubject.value,
              selectedLanguages.toList(),
              "",
              profile.value!!.uid,
              minPrice,
              maxPrice,
              0.0,
              "${selectedDate}T${selectedTime}:00",
              LessonStatus.PENDING,
          ),
          onComplete = {
            lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
            Toast.makeText(context, "Lesson added successfully", Toast.LENGTH_SHORT).show()
            navigationActions.navigateTo(Screen.HOME)
          })
    }
  }

  Scaffold(
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "Schedule a lesson",
                  style = MaterialTheme.typography.headlineMedium,
                  modifier = Modifier.testTag("Title"))
              IconButton(onClick = { navigationActions.navigateTo(Screen.HOME) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            }
      },
      content = { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(paddingValues)
                    .testTag("lessonContent")) {
              item {
                Text(
                    text = "Give a title and add a description to your lesson",
                    style = MaterialTheme.typography.titleSmall)
              }

              item {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Give a title to this lesson") },
                    placeholder = { Text("You can write what the lesson is about in short") },
                    modifier = Modifier.fillMaxWidth().testTag("titleField"),
                    singleLine = true)
              }

              item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Give a description to this lesson") },
                    placeholder = { Text("You can write what the lesson is about in detail") },
                    modifier = Modifier.fillMaxWidth().testTag("DescriptionField"),
                    singleLine = true)
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Select the desired date and time for the lesson",
                    style = MaterialTheme.typography.titleSmall)
              }

              item {
                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { datePickerDialog.show() },
                      modifier = Modifier.weight(1f).testTag("DateButton")) {
                        Text(
                            selectedDate.ifEmpty { "Select Date" },
                            style = MaterialTheme.typography.labelMedium)
                      }
                  Spacer(modifier = Modifier.width(12.dp))
                  Button(
                      onClick = { timePickerDialog.show() },
                      modifier = Modifier.weight(1f).testTag("TimeButton")) {
                        Text(
                            selectedTime.ifEmpty { "Select Time" },
                            style = MaterialTheme.typography.labelMedium)
                      }
                }
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Select the subject you want to study",
                    style = MaterialTheme.typography.titleSmall)
                SubjectSelector(selectedSubject)
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Select the possible languages for the course",
                    style = MaterialTheme.typography.titleSmall)
                LanguageSelector(selectedLanguages)
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
                PriceRangeSlider("Select a price range for your lesson:") { min, max ->
                  minPrice = min.toDouble()
                  maxPrice = max.toDouble()
                }
                Text("Selected price range: ${minPrice.toInt()} - ${maxPrice.toInt()}.-")
              }
            }
      },
      bottomBar = {
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().padding(14.dp).testTag("confirmButton")) {
              Text("Confirm your request", fontSize = 16.sp)
            }
      })
}

fun validateLessonInput(
    title: String,
    description: String,
    selectedSubject: MutableState<Subject>,
    selectedLanguages: List<Language>,
    date: String,
    time: String
): String? {
  for (entry in
      mapOf(
              "title" to title,
              "description" to description,
              "subject" to selectedSubject.value.name,
              "language" to selectedLanguages.joinToString { it.name },
              "date" to date,
              "time" to time,
          )
          .entries) {
    if (entry.value.isEmpty()) {
      return "${entry.key} is missing"
    }
  }
  return null
}
