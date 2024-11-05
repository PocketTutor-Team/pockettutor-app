package com.github.se.project.ui.components

import MapPickerBox
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Subject
import java.util.Calendar

@SuppressLint("NewApi")
@Composable
fun LessonEditor(
    mainTitle: String,
    profile: Profile,
    lesson: Lesson?,
    onBack: () -> Unit,
    onConfirm: (Lesson) -> Unit,
    onDelete: ((Lesson) -> Unit)? = null,
) {
  var title by remember { mutableStateOf(lesson?.title ?: "") }
  var description by remember { mutableStateOf(lesson?.description ?: "") }
  val selectedLanguages = remember { mutableStateListOf<Language>() }
  val selectedSubject = remember { mutableStateOf(lesson?.subject ?: Subject.NONE) }
  var minPrice by remember { mutableDoubleStateOf(lesson?.minPrice ?: 5.0) }
  var maxPrice by remember { mutableDoubleStateOf(lesson?.maxPrice ?: 50.0) }
  var selectedDate by remember { mutableStateOf("") }
  var selectedTime by remember { mutableStateOf("") }

  val calendar = Calendar.getInstance()
  val currentDateTime = Calendar.getInstance()
  val currentLessonId = remember { mutableStateOf<String?>(null) }

  // Default Location is assigned at EPFL
  var selectedLocation by remember {
    mutableStateOf(lesson?.let { it.latitude to it.longitude } ?: (46.520374 to 6.568339))
  }
  var isMapVisible by remember { mutableStateOf(false) }
  val onLocationSelected: (Pair<Double, Double>) -> Unit = { newLocation ->
    selectedLocation = newLocation
    isMapVisible = false // Hide map after confirming selection
  }

  if (currentLessonId.value != lesson?.id) {
    currentLessonId.value = lesson?.id
    if (lesson != null) {
      selectedLanguages.clear()
      selectedLanguages.addAll(lesson.languages)
      selectedTime = lesson.timeSlot.split("T")[1].substring(0, 5)
      selectedDate = lesson.timeSlot.split("T")[0]
    }
  }

  // Context for the Toast messages
  val context = LocalContext.current

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

  val onConfirmClick = {
    val error =
        validateLessonInput(
            title,
            description,
            selectedSubject,
            selectedLanguages,
            selectedDate,
            selectedTime,
            selectedLocation.first,
            selectedLocation.second)
    if (error != null) {
      Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    } else {
      Log.d("LessonEditor", lesson?.id ?: "Lesson is null")
      onConfirm(
          Lesson(
              lesson?.id ?: "",
              title,
              description,
              selectedSubject.value,
              selectedLanguages.toList(),
              "",
              profile.uid,
              minPrice,
              maxPrice,
              0.0,
              "${selectedDate}T${selectedTime}:00",
              LessonStatus.REQUESTED,
              selectedLocation.first,
              selectedLocation.second))
    }
  }

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
                  text = mainTitle,
                  modifier = Modifier.testTag("Title"),
                  style = MaterialTheme.typography.headlineMedium,
                  textAlign = TextAlign.Center)

              IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("lessonContent")
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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

              // Toggle button for Map Picker
              Button(onClick = { isMapVisible = !isMapVisible }) {
                Text(if (isMapVisible) "Hide Map" else "Pick Location on Map")
              }

              // Display selected location coordinates
              Text(
                  "Click on the Map to select the location",
              )

              // Conditional Map Picker display
              if (isMapVisible) {
                Box(modifier = Modifier.fillMaxWidth().height(400.dp).padding(top = 8.dp)) {
                  MapPickerBox(
                      initialLocation = selectedLocation, onLocationSelected = onLocationSelected)
                }
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
        Column {
          Button(
              modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
              shape = MaterialTheme.shapes.medium,
              onClick = onConfirmClick) {
                Text("Confirm")
              }
          if (onDelete != null) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("deleteButton"),
                shape = MaterialTheme.shapes.medium,
                onClick = { onDelete(lesson!!) }) {
                  Text("Delete")
                }
          }
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
  Log.d("LessonEditor", "Validating lesson input")
  for (entry in
      mapOf(
              "title" to title,
              "description" to description,
              "subject" to selectedSubject.value.name,
              "language" to selectedLanguages.joinToString { it.name },
              "date" to date,
              "time" to time,
              "latitude" to latitude.toString(),
              "longitude" to longitude.toString())
          .entries) {
    if (entry.value.isEmpty()) {
      return "${entry.key} is missing"
    }
  }
  Log.d("LessonEditor", "Lesson input is valid")
  return null
}
