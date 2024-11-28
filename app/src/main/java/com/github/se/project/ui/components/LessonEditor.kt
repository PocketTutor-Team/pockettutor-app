package com.github.se.project.ui.components

import MapPickerBox
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.map.LocationPermissionHandler
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun LessonEditor(
    mainTitle: String,
    profile: Profile,
    lesson: Lesson?,
    onBack: () -> Unit,
    onConfirm: (Lesson) -> Unit,
    onDelete: ((Lesson) -> Unit)? = null,
    onMapReady: (Boolean) -> Unit
) {
  var title by remember { mutableStateOf(lesson?.title ?: "") }
  var description by remember { mutableStateOf(lesson?.description ?: "") }
  val selectedLanguages = remember { mutableStateListOf<Language>() }
  val tutorUid = remember {
    mutableStateListOf<String>().apply { lesson?.tutorUid?.let { addAll(it) } }
  }
  val canBeInstant = remember { mutableStateOf(lesson == null) }
  val instant = remember { mutableStateOf(isInstant(lesson)) }
  val selectedSubject = remember { mutableStateOf(lesson?.subject ?: Subject.NONE) }
  var minPrice by remember { mutableDoubleStateOf(lesson?.minPrice ?: 5.0) }
  var maxPrice by remember { mutableDoubleStateOf(lesson?.maxPrice ?: 50.0) }
  var selectedDate by remember { mutableStateOf("") }
  var selectedTime by remember { mutableStateOf("") }

  val calendar = Calendar.getInstance()
  val currentDateTime = Calendar.getInstance()
  val currentLessonId = remember { mutableStateOf<String?>(null) }

  var selectedLocation by remember {
    mutableStateOf(lesson?.let { it.latitude to it.longitude } ?: (0.0 to 0.0))
  }
  var userLocation by remember { mutableStateOf<LatLng?>(null) }
  var isLocationChecked by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

  val cameraPositionState = rememberCameraPositionState {}

  var showMapDialog by remember { mutableStateOf(false) }

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

  LocationPermissionHandler { location ->
    userLocation = location
    isLocationChecked = true

    if (userLocation == null) {
      canBeInstant.value = false
    }
  }

    val datePickerState = rememberDatePickerState(calendar.getTimeInMillis(),
        selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Date().apply {
                time = utcTimeMillis
            }
            val currentDate = Date()

            // Reset the time of both dates to compare only the date (not the time)
            val calendarDate = Calendar.getInstance().apply { time = date }
            val calendarCurrentDate = Calendar.getInstance().apply { time = currentDate }

            // Set the time to midnight for both dates to ignore time component
            calendarDate.set(Calendar.HOUR_OF_DAY, 0)
            calendarDate.set(Calendar.MINUTE, 0)
            calendarDate.set(Calendar.SECOND, 0)
            calendarDate.set(Calendar.MILLISECOND, 0)

            calendarCurrentDate.set(Calendar.HOUR_OF_DAY, 0)
            calendarCurrentDate.set(Calendar.MINUTE, 0)
            calendarCurrentDate.set(Calendar.SECOND, 0)
            calendarCurrentDate.set(Calendar.MILLISECOND, 0)

            // Check if the date is today or in the future
            return calendarDate.time.after(calendarCurrentDate.time) || calendarDate.time == calendarCurrentDate.time
        }
    })

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true,
    )



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
    if (instant.value) {
      val lat: Double
      val lon: Double
      if (userLocation == null && (lesson?.longitude ?: 0.0) != 0.0) {
        lat = lesson!!.latitude
        lon = lesson.longitude
      } else {
        lat = userLocation?.latitude ?: 0.0
        lon = userLocation?.longitude ?: 0.0
      }
      val error =
          validateLessonInput(
              title,
              description,
              selectedSubject,
              selectedLanguages,
              "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}",
              "instant",
              lat,
              lon)
      if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
      } else {
        selectedDate =
            "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        onConfirm(
            Lesson(
                lesson?.id ?: "",
                title,
                description,
                selectedSubject.value,
                selectedLanguages.toList(),
                tutorUid,
                profile.uid,
                minPrice,
                maxPrice,
                0.0,
                "${selectedDate}Tinstant",
                lesson?.status ?: LessonStatus.INSTANT_REQUESTED,
                lat,
                lon))
      }
    } else {
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
        onConfirm(
            Lesson(
                lesson?.id ?: "",
                title,
                description,
                selectedSubject.value,
                selectedLanguages.toList(),
                tutorUid,
                profile.uid,
                minPrice,
                maxPrice,
                0.0,
                "${selectedDate}T${selectedTime}:00",
                if (lesson != null) lesson.status else LessonStatus.MATCHING,
                selectedLocation.first,
                selectedLocation.second))
      }
    }
  }
  // Format location for display
  val locationText =
      if (selectedLocation.first != 0.0 || selectedLocation.second != 0.0) {
        "Location selected"
      } else {
        "Select location"
      }

    if(showDatePicker){
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button({
                    val temp = Instant.ofEpochSecond((datePickerState.selectedDateMillis)?.div(
                        1000
                    ) ?: 0)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val zonedDateTime = temp.atZone(ZoneId.systemDefault())
                    selectedDate = formatter.format(zonedDateTime)
                    showDatePicker = false}) {
                    Text("Ok")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)) {
            DatePicker(datePickerState, showModeToggle = false)
        }
    }

    if(showTimeDialog) {
        Dialog(
            onDismissRequest = { showMapDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column {
                TimePicker(
                    state = timePickerState,
                )
                Button(onClick = { showTimeDialog = false }) {
                    Text("Dismiss picker")
                }
                Button(onClick = {
                    val selectedCalendar =
                        Calendar.getInstance().apply {
                            timeInMillis = currentDateTime.timeInMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }

                    val isSelectedDateToday =
                        selectedDate ==
                                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

                    if (isSelectedDateToday && selectedCalendar.before(currentDateTime)) {
                        Toast.makeText(context, "You cannot select a past time", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedTime = "${timePickerState.hour}:${timePickerState.minute}"
                    }
                }) {
                    Text("Confirm selection")
                }
            }
        }
    }
  // Map Dialog
  if (showMapDialog) {
    Dialog(
        onDismissRequest = { showMapDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
          Surface(
              modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(),
              shape = MaterialTheme.shapes.large) {
                Column {
                  // Dialog header
                  TopAppBar(
                      title = { Text("Select Location") },
                      navigationIcon = {
                        IconButton(onClick = { showMapDialog = false }) {
                          Icon(Icons.Default.Close, "Close map")
                        }
                      })

                  // Map content
                  Box() {
                    MapPickerBox(
                        initialLocation = selectedLocation,
                        onLocationSelected = { newLocation ->
                          selectedLocation = newLocation
                          showMapDialog = false
                        },
                        onMapReady = onMapReady)
                  }
                }
              }
        }
  }

  Scaffold(
      topBar = {
        Row(
            modifier =
                Modifier.testTag("topRow")
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = mainTitle,
                  modifier = Modifier.testTag("Title"),
                  style = MaterialTheme.typography.titleLarge)

              if (canBeInstant.value) {
                InstantButton(
                    isSelected = instant.value,
                    onToggle = { instant.value = it },
                    modifier = Modifier.testTag("instantButton"),
                    enabled = canBeInstant.value)
              }

              IconButton(onClick = onBack, modifier = Modifier.testTag("backButton")) {
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

              if (!instant.value) {
                Text(
                    "Select the desired date and time for the lesson",
                    style = MaterialTheme.typography.titleSmall)

                Row(modifier = Modifier.fillMaxWidth()) {
                  Button(
                      onClick = { showDatePicker = true },
                      modifier = Modifier.weight(1f).testTag("DateButton"),
                      colors =
                          ButtonDefaults.buttonColors(
                              containerColor = MaterialTheme.colorScheme.secondaryContainer,
                              contentColor = MaterialTheme.colorScheme.onPrimary)) {
                        Text(
                            selectedDate.ifEmpty { "Select Date" },
                            style = MaterialTheme.typography.titleSmall)
                      }

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                      onClick = { timePickerDialog.show() },
                      modifier = Modifier.weight(1f).testTag("TimeButton"),
                      colors =
                          ButtonDefaults.buttonColors(
                              containerColor = MaterialTheme.colorScheme.secondaryContainer,
                              contentColor = MaterialTheme.colorScheme.onPrimary)) {
                        Text(
                            selectedTime.ifEmpty { "Select Time" },
                            style = MaterialTheme.typography.titleSmall)
                      }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Select the location for the lesson",
                    style = MaterialTheme.typography.titleSmall)

                Button(
                    onClick = { showMapDialog = true },
                    modifier = Modifier.testTag("mapButton").fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary)) {
                      Icon(
                          if (selectedLocation.first != 0.0 || selectedLocation.second != 0.0)
                              Icons.Default.Check
                          else Icons.Default.LocationOn,
                          contentDescription = null,
                          modifier = Modifier.padding(end = 8.dp))
                      Text(locationText, style = MaterialTheme.typography.titleSmall)
                    }

                Spacer(modifier = Modifier.height(8.dp))
              }

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

              PriceRangeSlider(
                  "Select a price range for your lesson:",
                  { min, max ->
                    minPrice = min.toDouble()
                    maxPrice = max.toDouble()
                  },
                  initialStart = minPrice.toFloat(),
                  initialEnd = maxPrice.toFloat())

              Text("Selected price range: ${minPrice.toInt()}.- to ${maxPrice.toInt()}.-")
            }
      },
      bottomBar = {
        Column(
            modifier =
                Modifier.background(color = MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                  modifier = Modifier.fillMaxWidth().testTag("confirmButton"),
                  shape = MaterialTheme.shapes.medium,
                  onClick = onConfirmClick) {
                    Text("Confirm")
                  }

              if (onDelete != null) {
                Button(
                    modifier = Modifier.fillMaxWidth().testTag("deleteButton"),
                    shape = MaterialTheme.shapes.medium,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
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
  val requiredFields =
      mapOf(
          "title" to title,
          "description" to description,
          "subject" to selectedSubject.value.name,
          "language" to selectedLanguages.joinToString { it.name },
          "date" to date,
          "time" to time)

  // Check if any required field is empty
  for ((field, value) in requiredFields) {
    if (value.isEmpty()) {
      return "$field is missing"
    }
  }

  // Check if location has been set
  if (latitude == 0.0 && longitude == 0.0) {
    return "location is missing"
  }

  return null // All inputs are valid
}

fun isInstant(lesson: Lesson?): Boolean {
  return (lesson?.status == LessonStatus.INSTANT_REQUESTED) ?: false ||
      (lesson?.status == LessonStatus.INSTANT_CONFIRMED) ?: false
}

fun isInstant(timeSlot: String): Boolean {
  return timeSlot.last() == 't'
}
