package com.github.se.project.ui.lesson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.lesson.Lesson
import com.android.sample.model.lesson.LessonStatus
import com.android.sample.model.lesson.LessonsViewModel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.TutoringSubject
import com.github.se.project.ui.components.WritableDropdown
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import java.util.Calendar

@Composable
fun AddLessonScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonsViewModel = viewModel(factory = LessonsViewModel.Factory)
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var subject by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("") }
  var minPrice by remember { mutableStateOf(0.0) }
  var maxPrice by remember { mutableStateOf(0.0) }
  var timeSlot by remember { mutableStateOf("") }
  val calendar = Calendar.getInstance()

  val profile = listProfilesViewModel.currentProfile.collectAsState()
  val lessonState = lessonViewModel.lessons

  // Context for the Toast messages
  val context = LocalContext.current

  var selectedDate by remember { mutableStateOf("") }
  var selectedTime by remember { mutableStateOf("") }

  val onConfirm = {
    val error =
        validateLessonInput(
            title,
            description,
            subject,
            language,
            minPrice,
            maxPrice,
            selectedDate,
            selectedTime,
        )
    if (error != null) {
      Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    } else {
      lessonViewModel.addLesson(
          profile.value!!.uid,
          Lesson(
              lessonViewModel.getNewUid(),
              title,
              description,
              TutoringSubject.valueOf(subject),
              "",
              profile.value!!.uid,
              minPrice,
              maxPrice,
              "${selectedDate}T${selectedTime}:00",
              LessonStatus.REQUESTED,
              language))

      // this should ideally be done in onSuccess callback of addLesson
      // but done directly here in the meantime
      navigationActions.navigateTo(Screen.HOME)
    }
  }

  // Date Picker
  val datePickerDialog =
      DatePickerDialog(
          context,
          { _, year, month, dayOfMonth -> selectedDate = "$dayOfMonth/${month + 1}/$year" },
          calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DAY_OF_MONTH))

  // Time Picker
  val timePickerDialog =
      TimePickerDialog(
          context,
          { _, hourOfDay, minute ->
            val formattedHour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
            selectedTime = "$formattedHour:$formattedMinute"
          },
          calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE),
          true // 24-hour format
          )

  Scaffold(
      topBar = {
        Text(
            text = "Schedule a new lesson",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp))
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                  value = title,
                  onValueChange = { title = it },
                  label = { Text("You can write what the lesson is about in short") },
                  placeholder = { Text("Give a title to this lesson") },
                  modifier = Modifier.fillMaxWidth(),
                  singleLine = true)

              // TODO: description picture

              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("You can write what the lesson is about in detail") },
                  placeholder = { Text("Give a description to this lesson") },
                  modifier = Modifier.fillMaxWidth(),
                  singleLine = true)

              Text("Select the desired date and time for the lesson")
              Button(onClick = { datePickerDialog.show() }) {
                Text(if (selectedDate.isEmpty()) "Select Date" else "Selected Date: $selectedDate")
              }

              Button(onClick = { timePickerDialog.show() }) {
                Text(if (selectedTime.isEmpty()) "Select Time" else "Selected Time: $selectedTime")
              }

              // Subject input
              WritableDropdown(
                  label = "Which subject would you like to study ?",
                  placeholder = "Subject(s)",
                  value = subject,
                  onValueChange = { subject = it },
                  choices = TutoringSubject.entries.map { it.name }.toList())

              // Language input
              WritableDropdown(
                  label = "In which language would you like the lesson to take place ?",
                  placeholder = "Language(s)",
                  value = language,
                  onValueChange = { language = it },
                  choices = Language.entries.map { it.name }.toList())

              // Slider price selection
              PriceSlider("Select a price range for your lesson:") { min, max ->
                minPrice = min.toDouble()
                maxPrice = max.toDouble()
              }
              Text("Selected price range: ${minPrice.toInt()}.- to ${maxPrice.toInt()}.-")
            }
      },
      bottomBar = {
        Button(modifier = Modifier.fillMaxWidth().padding(16.dp), onClick = onConfirm) {
          Text("Confirm your details")
        }
      })
}

/** Return a potential validation error */
fun validateLessonInput(
    title: String,
    description: String,
    subject: String,
    language: String,
    minPrice: Double,
    maxPrice: Double,
    date: String,
    time: String
): String? {
  for (entry in
      mapOf(
              "title" to title,
              "description" to description,
              "subject" to subject,
              "language" to language,
              "date" to date,
              "time" to time,
          )
          .entries) {
    if (entry.value.isEmpty()) {
      return "${entry.key} is missing"
    }
  }
  try {
    TutoringSubject.valueOf(subject)
  } catch (e: IllegalArgumentException) {
    return "Invalid subject"
  }
  return null
}

@Composable
fun PriceSlider(label: String, onValueChange: (Float, Float) -> Unit) {
  var sliderPosition by remember { mutableStateOf(0f..100f) }
  Column {
    Text(text = label)
    RangeSlider(
        value = sliderPosition,
        steps = 45,
        onValueChange = { range -> sliderPosition = range },
        valueRange = 5f..50f,
        onValueChangeFinished = {
          onValueChange(sliderPosition.start, sliderPosition.endInclusive)
        },
    )
  }
}
