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
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import java.util.Calendar

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
    val selectedLocation by lessonViewModel.selectedLocation.collectAsState()

  // Context for the Toast messages
  val context = LocalContext.current

  var selectedDate by remember { mutableStateOf("") }
  var selectedTime by remember { mutableStateOf("") }

  val onConfirm = {
    val error =
        validateLessonInput(
            title,
            description,
            selectedSubject,
            selectedLanguages,
            selectedDate,
            selectedTime,
            selectedLocation.first,
            selectedLocation.second
        )
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
              LessonStatus.REQUESTED,
              selectedLocation.first,
              selectedLocation.second
          ),
          onComplete = {
            lessonViewModel.getLessonsForStudent(profile.value!!.uid, onComplete = {})
            Toast.makeText(context, "Lesson added successfully", Toast.LENGTH_SHORT).show()
          })

      navigationActions.navigateTo(Screen.HOME)
    }
  }



  // Date Picker
  val datePickerDialog =
      DatePickerDialog(
          context,
          { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedDate = "$dayOfMonth/${month + 1}/$year"
          },
          calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH),
          calendar.get(Calendar.DAY_OF_MONTH))
  datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis

  // Time Picker
  val timePickerDialog =
      TimePickerDialog(
          context,
          { _, hourOfDay, minute ->
            val formattedHour = hourOfDay.toString().padStart(2, '0')
            val formattedMinute = minute.toString().padStart(2, '0')

            val selectedCalendar =
                Calendar.getInstance().apply {
                  timeInMillis = currentDateTime.timeInMillis
                  set(Calendar.HOUR_OF_DAY, hourOfDay)
                  set(Calendar.MINUTE, minute)
                }

            val isSelectedDateToday =
                selectedDate ==
                    "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

            if (isSelectedDateToday && selectedCalendar.before(currentDateTime)) {
              Toast.makeText(context, "You cannot select a past time", Toast.LENGTH_SHORT).show()
            } else {
              selectedTime = "$formattedHour:$formattedMinute"
            }
          },
          calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE),
          true)

  Scaffold(
      topBar = {
        Row(
            modifier =
                Modifier.testTag("topRow")
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "Schedule a lesson",
                  modifier = Modifier.testTag("Title"),
                  style = MaterialTheme.typography.headlineMedium,
                  textAlign = TextAlign.Center)

              IconButton(onClick = { navigationActions.navigateTo(Screen.HOME) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("bigColumn")
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  "Give a title and add a description to your lesson",
                  style = MaterialTheme.typography.titleSmall)

              OutlinedTextField(
                  value = title,
                  onValueChange = { title = it },
                  label = { Text("Give a title to this lesson") },
                  placeholder = { Text("You can write what the lesson is about in short") },
                  modifier = Modifier.fillMaxWidth().testTag("titleField"),
                  singleLine = true)

              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text("Give a description to this lesson") },
                  placeholder = { Text("You can write what the lesson is about in detail") },
                  modifier = Modifier.fillMaxWidth().testTag("DescriptionField"),
                  singleLine = true)

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                  "Select the desired date and time for the lesson",
                  style = MaterialTheme.typography.titleSmall)

              Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f).testTag("DateButton"),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                      Text(
                          selectedDate.ifEmpty { "Select Date" },
                          style = MaterialTheme.typography.labelMedium)
                    }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.weight(1f).testTag("TimeButton"),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                      Text(
                          selectedTime.ifEmpty { "Select Time" },
                          style = MaterialTheme.typography.labelMedium)
                    }
              }

              Spacer(modifier = Modifier.height(8.dp))

            // Navigation to the map picker screen to select the location of Lesson
            Button(onClick = {
                navigationActions.navigateTo(Screen.MAP_LOC_PICKER)
            }) {
                Text("Pick Location")
            }

            // Show selected location if available
            selectedLocation.let { (latitude, longitude) ->
                Text("Selected Location: Lat: $latitude, Lng: $longitude")
            }

          Spacer(modifier = Modifier.height(8.dp))

            Text(
                  "Select the subject you want to study",
                  style = MaterialTheme.typography.titleSmall)
              SubjectSelector(selectedSubject)

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                  "Select the possible languages you want the course to take place in",
                  style = MaterialTheme.typography.titleSmall)
              LanguageSelector(selectedLanguages)

              Spacer(modifier = Modifier.height(8.dp))

              PriceRangeSlider("Select a price range for your lesson:") { min, max ->
                minPrice = min.toDouble()
                maxPrice = max.toDouble()
              }

              Text("Selected price range: ${minPrice.toInt()}.- to ${maxPrice.toInt()}.-")
            }
      },
      bottomBar = {
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = onConfirm) {
              Text("Confirm your request")
            }
      })
}

fun validateLessonInput(
    title: String,
    description: String,
    selectedSubject: MutableState<Subject>,
    selectedLanguages: List<Language>,
    date: String,
    time: String,
    latitude: Double,
    longitude: Double
): String? {
  for (entry in
      mapOf(
              "title" to title,
              "description" to description,
              "subject" to selectedSubject.value.name,
              "language" to selectedLanguages.joinToString { it.name },
              "date" to date,
              "time" to time,
                "latitude" to latitude.toString(),
                "longitude" to longitude.toString()
          )
          .entries) {
    if (entry.value.isEmpty()) {
      return "${entry.key} is missing"
    }
  }
  return null
}
