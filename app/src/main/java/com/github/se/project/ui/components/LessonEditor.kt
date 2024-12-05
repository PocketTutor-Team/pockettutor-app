package com.github.se.project.ui.components

import MapPickerBox
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.map.LocationPermissionHandler
import com.google.android.gms.maps.model.LatLng
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

/**
 * A composable function for creating or editing a lesson.
 *
 * This function provides an interactive user interface to configure lesson details, including
 * title, description, subject, languages, pricing, date, time, and location. It supports editing
 * existing lessons or creating new ones, including instant lessons.
 *
 * @param mainTitle Title to display at the top of the screen.
 * @param profile The user's profile information.
 * @param lesson The Lesson object being edited, or null if creating a new lesson.
 * @param onBack Callback to execute when the user navigates back.
 * @param onConfirm Callback to execute when the user confirms their input.
 * @param onDelete Optional callback for deleting a lesson.
 * @param onMapReady Callback for when the map is fully initialized.
 */
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
  // State variables for storing lesson data. Initial values are set based on the provided lesson.
  var title by remember { mutableStateOf(lesson?.title ?: "") } // Lesson title
  var description by remember { mutableStateOf(lesson?.description ?: "") } // Lesson description
  val selectedLanguages = remember {
    mutableStateListOf<Language>()
  } // Languages offered in the lesson
  val tutorUid = remember { // List of tutor IDs associated with the lesson
    mutableStateListOf<String>().apply { lesson?.tutorUid?.let { addAll(it) } }
  }
  val canBeInstant = remember {
    mutableStateOf(lesson == null)
  } // Determines if the lesson can be instant
  val instant = remember { mutableStateOf(isInstant(lesson)) } // Indicates if the lesson is instant
  val selectedSubject = remember {
    mutableStateOf(lesson?.subject ?: Subject.NONE)
  } // Lesson subject
  var minPrice by remember { mutableDoubleStateOf(lesson?.minPrice ?: 5.0) } // Minimum price
  var maxPrice by remember { mutableDoubleStateOf(lesson?.maxPrice ?: 50.0) } // Maximum price
  var selectedDate by remember { mutableStateOf("") } // Selected date
  var selectedTime by remember { mutableStateOf("") } // Selected time

  // Calendar instances for date and time management
  val calendar = Calendar.getInstance()
  val currentDateTime = Calendar.getInstance()

  // Holds the currently edited lesson ID for initialization checks
  val currentLessonId = remember { mutableStateOf<String?>(null) }

  // State variables for location management
  var selectedLocation by remember {
    mutableStateOf(lesson?.let { it.latitude to it.longitude } ?: (0.0 to 0.0)) // Lesson location
  }
  var userLocation by remember { mutableStateOf<LatLng?>(null) } // Current user location
  var isLocationChecked by remember { mutableStateOf(false) } // Indicates if location is verified

  // State variables for managing dialogs
  var showDatePicker by remember { mutableStateOf(false) } // Show date picker dialog
  var showTimeDialog by remember { mutableStateOf(false) } // Show time picker dialog
  var showMapDialog by remember { mutableStateOf(false) } // Show map selection dialog
  
  if (currentLessonId.value != lesson?.id) {
    currentLessonId.value = lesson?.id
    if (lesson != null) {
      selectedLanguages.clear() // Clears and updates selected languages
      selectedLanguages.addAll(lesson.languages)
      selectedTime =
          lesson.timeSlot.split("T")[1].substring(0, 5) // Extracts time from lesson time slot
      selectedDate = lesson.timeSlot.split("T")[0] // Extracts date from lesson time slot
    }
  }

  // Retrieves the current context for displaying messages (e.g., Toasts)
  val context = LocalContext.current

  // Handles location permissions and updates the user's location if allowed
  LocationPermissionHandler { location ->
    userLocation = location // Updates user location if permissions are granted
    isLocationChecked = true

    // Ensures the lesson cannot be instant if location is unavailable
    if (userLocation == null) {
      canBeInstant.value = false
    }
  }

  // Date picker state with restrictions on selectable dates
  val datePickerState =
      rememberDatePickerState(
          calendar.timeInMillis,
          selectableDates =
              object : SelectableDates {
                // Ensures only dates within one year of today are selectable
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                  val date = Date(utcTimeMillis)
                  val currentDate = Date()

                  val calendarDate = Calendar.getInstance().apply { time = date }
                  val calendarCurrentDate = Calendar.getInstance().apply { time = currentDate }

                  // Resets time components for accurate date comparison
                  calendarDate.set(Calendar.HOUR_OF_DAY, 0)
                  calendarDate.set(Calendar.MINUTE, 0)
                  calendarDate.set(Calendar.SECOND, 0)
                  calendarDate.set(Calendar.MILLISECOND, 0)

                  calendarCurrentDate.set(Calendar.HOUR_OF_DAY, 0)
                  calendarCurrentDate.set(Calendar.MINUTE, 0)
                  calendarCurrentDate.set(Calendar.SECOND, 0)
                  calendarCurrentDate.set(Calendar.MILLISECOND, 0)

                  // Allows selection only within the next year
                  val oneYearFromNow =
                      Calendar.getInstance().apply {
                        time = currentDate
                        add(Calendar.YEAR, 1)
                      }

                  return calendarDate.time.after(calendarCurrentDate.time) &&
                      calendarDate.time.before(oneYearFromNow.time)
                }
              })

  // Time picker state for selecting hours and minutes
  val timePickerState =
      rememberTimePickerState(
          initialHour = calendar.get(Calendar.HOUR_OF_DAY), // Default hour
          initialMinute = calendar.get(Calendar.MINUTE), // Default minute
          is24Hour = true, // 24-hour format
      )

  // Handles the confirm action by validating inputs and creating/updating the lesson
  val onConfirmClick = {
    if (instant.value) { // Checks if the lesson is an instant lesson
      val lat: Double
      val lon: Double

      // Uses current or stored location data
      if (userLocation == null && (lesson?.longitude ?: 0.0) != 0.0) {
        lat = lesson!!.latitude
        lon = lesson.longitude
      } else {
        lat = userLocation?.latitude ?: 0.0
        lon = userLocation?.longitude ?: 0.0
      }

      // Validates inputs and creates an instant lesson
      val error =
          validateLessonInput(
              title,
              description,
              selectedSubject,
              selectedLanguages,
              "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}",
              "instant",
              lat,
              lon,
              context)
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
      // Validates inputs and creates a scheduled lesson
      val error =
          validateLessonInput(
              title,
              description,
              selectedSubject,
              selectedLanguages,
              selectedDate,
              selectedTime,
              selectedLocation.first,
              selectedLocation.second,
              context)
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
                lesson?.status ?: LessonStatus.MATCHING,
                selectedLocation.first,
                selectedLocation.second))
      }
    }
  }
  // Format location for display
  val locationText =
      if (selectedLocation.first != 0.0 || selectedLocation.second != 0.0) {
        stringResource(R.string.location_selected)
      } else {
        stringResource(R.string.select_location)
      }

  if (showDatePicker) {
    DatePickerDialog(
        onDismissRequest = { showDatePicker = false },
        confirmButton = {
          Button({
            val temp = Instant.ofEpochSecond((datePickerState.selectedDateMillis)?.div(1000) ?: 0)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val zonedDateTime = temp.atZone(ZoneId.systemDefault())
            selectedDate = formatter.format(zonedDateTime)
            showDatePicker = false
          }) {
            Text(stringResource(R.string.ok))
          }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
          DatePicker(datePickerState, showModeToggle = false)
        }
  }

  if (showTimeDialog) {
    Dialog(
        onDismissRequest = { showMapDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
          Column {
            TimePicker(
                state = timePickerState,
            )
            Button(onClick = { showTimeDialog = false }) { Text(stringResource(R.string.close)) }
            Button(
                onClick = {
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
                    Toast.makeText(
                            context, context.getString(R.string.past_time), Toast.LENGTH_SHORT)
                        .show()
                  } else {
                    selectedTime = "${timePickerState.hour}:${timePickerState.minute}"
                    showTimeDialog = false
                  }
                }) {
                  Text(stringResource(R.string.ok))
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
                      title = { Text(stringResource(R.string.select_location)) },
                      navigationIcon = {
                        IconButton(onClick = { showMapDialog = false }) {
                          Icon(Icons.Default.Close, "Close map")
                        }
                      })

                  // Map content
                  Box {
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
              Text(stringResource(R.string.title_desc), style = MaterialTheme.typography.titleSmall)

              OutlinedTextField(
                  value = title,
                  onValueChange = { title = it },
                  label = { Text(stringResource(R.string.give_title)) },
                  placeholder = { Text(stringResource(R.string.title_placeholder)) },
                  modifier = Modifier.fillMaxWidth().testTag("titleField"),
                  singleLine = true)

              OutlinedTextField(
                  value = description,
                  onValueChange = { description = it },
                  label = { Text(stringResource(R.string.give_description)) },
                  placeholder = { Text(stringResource(R.string.description_placeholder)) },
                  modifier = Modifier.fillMaxWidth().testTag("DescriptionField"),
                  singleLine = true)

              Spacer(modifier = Modifier.height(8.dp))

              if (!instant.value) {
                Text(
                    stringResource(R.string.select_date_time),
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
                            selectedDate.ifEmpty { stringResource(R.string.select_date) },
                            style = MaterialTheme.typography.titleSmall)
                      }

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                      onClick = { showTimeDialog = true },
                      modifier = Modifier.weight(1f).testTag("TimeButton"),
                      colors =
                          ButtonDefaults.buttonColors(
                              containerColor = MaterialTheme.colorScheme.secondaryContainer,
                              contentColor = MaterialTheme.colorScheme.onPrimary)) {
                        Text(
                            selectedTime.ifEmpty { stringResource(R.string.select_time) },
                            style = MaterialTheme.typography.titleSmall)
                      }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    stringResource(R.string.give_location),
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
                  stringResource(R.string.give_subject),
                  style = MaterialTheme.typography.titleSmall)
              SubjectSelector(selectedSubject)

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                  stringResource(R.string.give_language),
                  style = MaterialTheme.typography.titleSmall)
              LanguageSelector(selectedLanguages)

              Spacer(modifier = Modifier.height(8.dp))

              PriceRangeSlider(
                  stringResource(R.string.give_price),
                  { min, max ->
                    minPrice = min.toDouble()
                    maxPrice = max.toDouble()
                  },
                  initialStart = minPrice.toFloat(),
                  initialEnd = maxPrice.toFloat())

              Text(
                  "${stringResource(R.string.selected_price_range)} ${minPrice.toInt()}.- to ${maxPrice.toInt()}.-")
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
                    Text(stringResource(R.string.confirm))
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
                      Text(stringResource(R.string.delete))
                    }
              }
            }
      })
}

/**
 * Validates the input fields required for creating or editing a lesson.
 *
 * This function checks whether all mandatory fields (title, description, subject, languages, date,
 * time, and location) have been provided and are valid.
 *
 * @param title The title of the lesson.
 * @param description The description of the lesson.
 * @param selectedSubject The subject of the lesson wrapped in a MutableState object.
 * @param selectedLanguages A list of languages associated with the lesson.
 * @param date The selected date for the lesson.
 * @param time The selected time for the lesson.
 * @param latitude The latitude of the lesson's location.
 * @param longitude The longitude of the lesson's location.
 * @return A string describing the first missing field, or `null` if all inputs are valid.
 */
fun validateLessonInput(
    title: String,
    description: String,
    selectedSubject: MutableState<Subject>,
    selectedLanguages: List<Language>,
    date: String,
    time: String,
    latitude: Double,
    longitude: Double,
    context: Context
): String? {
  // Create a map of required fields with their corresponding values for validation
  val requiredFields =
      mapOf(
          "title" to title, // Lesson title
          "description" to description, // Lesson description
          "subject" to selectedSubject.value.name, // Selected subject name
          "language" to
              selectedLanguages.joinToString { it.name }, // Joined names of selected languages
          "date" to date, // Selected date
          "time" to time // Selected time
          )

  // Iterate through each field and value to check if any are empty
  for ((field, value) in requiredFields) {
    if (value.isEmpty()) {
      return "$field ${context.getString(R.string.is_missing)}"
    }
  }

  // Ensure a valid location is set (latitude and longitude must not be default values)
  if (latitude == 0.0 && longitude == 0.0) {
    return context.getString(R.string.missing_location)
  }

  return null // All fields are valid, return null
}

/**
 * Determines if a given lesson is an "instant" lesson based on its status.
 *
 * Instant lessons are those that are either requested or confirmed for immediate scheduling.
 *
 * @param lesson The lesson to check, or null.
 * @return `true` if the lesson has an instant status, `false` otherwise.
 */
fun isInstant(lesson: Lesson?): Boolean {
  return if (lesson == null) false
  else
      (lesson.status == LessonStatus.INSTANT_REQUESTED) ||
          (lesson.status == LessonStatus.INSTANT_CONFIRMED)
}

/**
 * Determines if a given time slot represents an "instant" lesson.
 *
 * An instant lesson's time slot typically ends with the letter 't'.
 *
 * @param timeSlot The time slot string to check.
 * @return `true` if the time slot indicates an instant lesson, `false` otherwise.
 */
fun isInstant(timeSlot: String): Boolean {
  return timeSlot.last() == 't' // Check if the last character in the time slot is 't'
}
