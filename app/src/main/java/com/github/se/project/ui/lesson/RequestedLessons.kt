@file:Suppress("NAME_SHADOWING")

package com.github.se.project.ui.lesson

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.lesson.SuitabilityScoreCalculator
import com.github.se.project.model.lesson.SuitabilityScoreCalculator.computeLanguageMatch
import com.github.se.project.model.lesson.SuitabilityScoreCalculator.computeSubjectMatch
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.components.InstantButton
import com.github.se.project.ui.components.calculateDistance
import com.github.se.project.ui.components.getColorForDistance
import com.github.se.project.ui.components.isInstant
import com.github.se.project.ui.map.LocationPermissionHandler
import com.github.se.project.ui.navigation.*
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen displaying all requested lessons that tutors can respond to. Includes filtering by date
 * and subject, and detailed lesson information.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RequestedLessonsScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
  // State management
  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var selectedSubject by remember { mutableStateOf<Subject?>(null) }
  var showFilterDialog by remember { mutableStateOf(false) }

  val canSeeInstants = remember { mutableStateOf(false) }
  val showInstants = remember { mutableStateOf(false) }
  val maxDistance = remember { mutableStateOf(1500) }
  val distanceSliderOpen = remember { mutableStateOf(false) }

  // Collect states
  val currentProfile by listProfilesViewModel.currentProfile.collectAsState()
  val requestedLessons by lessonViewModel.requestedLessons.collectAsState()

  var userLocation by remember { mutableStateOf<LatLng?>(null) }
  var isLocationChecked by remember { mutableStateOf(false) }

  LocationPermissionHandler { location ->
    userLocation = location
    isLocationChecked = true

    if (userLocation != null) {
      canSeeInstants.value = true
      showInstants.value = false
    }
  }

  // Update lessons when screen is launched
  LaunchedEffect(Unit) {
    lessonViewModel.getAllRequestedLessons()
    listProfilesViewModel.getProfiles()
  }

  // Filter lessons
  var filteredLessonsWithScores =
      requestedLessons
          .filter { lesson ->
            val dateMatches =
                selectedDate?.let { date ->
                  parseLessonDate(lesson.timeSlot)?.toLocalDate() == date
                } ?: true

            val subjectMatches =
                selectedSubject?.let { subject -> lesson.subject == subject } ?: true

            val notAlreadyResponded = !lesson.tutorUid.contains(currentProfile?.uid)

            val instantaneityCorresponds = isInstant(lesson) == showInstants.value

            val lessonLocation = LatLng(lesson.latitude, lesson.longitude)

            val distanceFilter =
                (!showInstants.value) ||
                    (maxDistance.value == 5100) ||
                    calculateDistance(userLocation, lessonLocation) < maxDistance.value

            val tutorSubjectMatches = computeSubjectMatch(lesson, currentProfile!!)
            val languageMatches = computeLanguageMatch(lesson, currentProfile!!)

            dateMatches &&
                subjectMatches &&
                notAlreadyResponded &&
                instantaneityCorresponds &&
                distanceFilter &&
                tutorSubjectMatches &&
                languageMatches
          }
          .mapNotNull { lesson ->
            val studentProfile =
                listProfilesViewModel.profiles.value.find { it.uid == lesson.studentUid } ?: null
            val tutorProfile = currentProfile ?: return@mapNotNull null

            val score =
                SuitabilityScoreCalculator.calculateSuitabilityScore(
                    lesson, userLocation, tutorProfile, studentProfile)
            lesson to score
          }
          .sortedByDescending { it.second }

  if (showInstants.value) {
    filteredLessonsWithScores =
        filteredLessonsWithScores.sortedBy {
          calculateDistance(userLocation, LatLng(it.first.latitude, it.first.longitude))
        }
  } else {
    filteredLessonsWithScores = filteredLessonsWithScores.sortedBy { it.second }.reversed()
  }

  var refreshing by remember { mutableStateOf(false) }
  val refreshScope = rememberCoroutineScope()

  fun refresh() =
      refreshScope.launch {
        refreshing = true
        lessonViewModel.getAllRequestedLessons()
        listProfilesViewModel.getProfiles()
        delay(1000)
        refreshing = false
      }

  val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

  Scaffold(
      topBar = {
        LessonsRequestedTopBar(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onFilterClick = { showFilterDialog = true },
            canSeeInstants = canSeeInstants,
            instant = showInstants,
            onDistanceClick = { distanceSliderOpen.value = true })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { navigationActions.navigateTo(it) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS_TUTOR,
            selectedItem = navigationActions.currentRoute())
      }) { padding ->
        var refreshing by remember { mutableStateOf(false) }
        val refreshScope = rememberCoroutineScope()

        fun refresh() =
            refreshScope.launch {
              refreshing = true
              lessonViewModel.getAllRequestedLessons()
              listProfilesViewModel.getProfiles()
              delay(1000)
              refreshing = false
            }

        val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

        Box(modifier = Modifier.fillMaxSize().padding(padding).pullRefresh(pullRefreshState)) {
          Column(modifier = Modifier.fillMaxSize()) {
            if (selectedSubject != null) {
              FilterChip(
                  selected = true,
                  onClick = { selectedSubject = null },
                  label = { Text(selectedSubject?.name ?: "All Subjects") },
                  leadingIcon = { Icon(Icons.Default.Clear, "Clear filter") },
                  modifier = Modifier.padding(horizontal = 16.dp),
                  colors =
                      FilterChipDefaults.filterChipColors(
                          selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                          selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary))
            }

            if (filteredLessonsWithScores.isEmpty()) {
              EmptyState(showInstants)
            } else {
              LazyColumn(
                  modifier = Modifier.fillMaxSize().testTag("lessonsList"),
                  contentPadding = PaddingValues(horizontal = 16.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredLessonsWithScores.size) { index ->
                      val (lesson, score) = filteredLessonsWithScores[index]
                      if (!showInstants.value) {
                        DisplayLessons(
                            lessons = listOf(lesson),
                            isTutor = (currentProfile?.role == Role.TUTOR),
                            onCardClick = { lessonClicked ->
                              lessonViewModel.selectLesson(lessonClicked)
                              navigationActions.navigateTo(Screen.TUTOR_LESSON_RESPONSE)
                            },
                            listProfilesViewModel = listProfilesViewModel,
                            requestedScreen = true,
                            suitabilityScore = score)
                      } else {
                        DisplayLessons(
                            lessons = listOf(lesson),
                            isTutor = (currentProfile?.role == Role.TUTOR),
                            onCardClick = { lessonClicked ->
                              lessonViewModel.selectLesson(lessonClicked)
                              navigationActions.navigateTo(Screen.TUTOR_LESSON_RESPONSE)
                            },
                            listProfilesViewModel = listProfilesViewModel,
                            requestedScreen = true,
                            distance =
                                calculateDistance(
                                        userLocation, LatLng(lesson.latitude, lesson.longitude))
                                    .toInt())
                      }
                    }
                  }
            }
          }

          PullRefreshIndicator(
              refreshing = refreshing,
              state = pullRefreshState,
              modifier = Modifier.align(Alignment.TopCenter),
              backgroundColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.primary)

          if (showFilterDialog) {
            FilterDialog(
                currentSubject = selectedSubject,
                onSubjectSelected = { selectedSubject = it },
                onDismiss = { showFilterDialog = false })
          }

          if (distanceSliderOpen.value) {
            DistanceDialog(
                currentDistance = maxDistance,
                onDismiss = { distanceSliderOpen.value = false },
                onApply = { distance ->
                  maxDistance.value = distance
                  distanceSliderOpen.value = false
                })
          }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsRequestedTopBar(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
    canSeeInstants: MutableState<Boolean>,
    instant: MutableState<Boolean>,
    onDistanceClick: () -> Unit,
) {
  val context = LocalContext.current
  val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

  // Date picker dialog setup
  val calendar = Calendar.getInstance()
  val datePickerDialog =
      DatePickerDialog(
              context,
              { _, year, month, dayOfMonth ->
                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(newDate)
              },
              calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH))
          .apply { datePicker.minDate = Calendar.getInstance().timeInMillis }

  TopAppBar(
      modifier = Modifier.testTag("topBar"),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Lessons Requested", modifier = Modifier.testTag("screenTitle"))
          Spacer(modifier = Modifier.width(8.dp))
        }
      },
      colors =
          TopAppBarDefaults.topAppBarColors()
              .copy(containerColor = MaterialTheme.colorScheme.background),
      actions = {
        if (canSeeInstants.value) {
          InstantButton(isSelected = instant.value, onToggle = { instant.value = it })
        }
        if (instant.value) {
          IconButton(onClick = onDistanceClick, modifier = Modifier.testTag("distanceButton")) {
            Icon(Icons.Outlined.LocationOn, "Distance")
          }
        }
        // Date picker button
        if (!instant.value) {
          Surface(
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable { datePickerDialog.show() }.testTag("datePicker"),
              color = MaterialTheme.colorScheme.background) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                      Icon(
                          Icons.Default.DateRange,
                          "Calendar",
                          tint = MaterialTheme.colorScheme.primary)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(selectedDate?.format(dateFormatter) ?: "-/-")
                    }
              }
        }

        // Filter button
        IconButton(onClick = onFilterClick, modifier = Modifier.testTag("filterButton")) {
          Icon(Icons.Outlined.Menu, "Filter")
        }
      })
}

@Composable
private fun FilterDialog(
    currentSubject: Subject?,
    onSubjectSelected: (Subject?) -> Unit,
    onDismiss: () -> Unit
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Filter by subject") },
      text = {
        Column {
          Subject.values()
              .filter { it != Subject.NONE }
              .forEach { subject ->
                Row(
                    modifier =
                        Modifier.fillMaxWidth().clickable {
                          onSubjectSelected(subject)
                          onDismiss()
                        },
                    verticalAlignment = Alignment.CenterVertically) {
                      RadioButton(
                          selected = subject == currentSubject,
                          onClick = {
                            onSubjectSelected(subject)
                            onDismiss()
                          })
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(subject.name, style = MaterialTheme.typography.titleSmall)
                    }
              }
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              onSubjectSelected(null)
              onDismiss()
            }) {
              Text("Clear filter")
            }
      },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun DistanceDialog(
    currentDistance: MutableState<Int>,
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit
) {
  var tempDistance by remember { mutableIntStateOf(currentDistance.value) }
  val isSystemInDark = isSystemInDarkTheme()

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Choose the maximum distance") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          Slider(
              value = tempDistance.toFloat(),
              onValueChange = { tempDistance = it.toInt() },
              valueRange = 100f..5100f,
              steps = 49,
              modifier = Modifier.padding(horizontal = 16.dp),
              colors =
                  SliderDefaults.colors(
                      activeTrackColor = getColorForDistance(tempDistance, isSystemInDark),
                      thumbColor = getColorForDistance(tempDistance, isSystemInDark)))

          Text(
              text = if (tempDistance == 5100) "No limit" else "Maximum distance: ${tempDistance}m",
              color = getColorForDistance(tempDistance, isSystemInDark))
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              onApply(tempDistance)
              onDismiss()
            }) {
              Text("Apply filter")
            }
      },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}

@Composable
private fun EmptyState(showInstant: MutableState<Boolean>) {
  Column(
      modifier = Modifier.fillMaxSize().padding(32.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        if (showInstant.value) {
          Text(
              text = "No instant lessons currently pending",
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("noLessonsMessage"))
          Button({ showInstant.value = false }) { Text("Show all lessons") }
        } else {
          Text(
              text = "No lessons available",
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("noLessonsMessage"))
          Text(
              text = "Try adjusting your filters or check back later",
              style = MaterialTheme.typography.bodyMedium,
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
}

/**
 * Parses a lesson's time slot string into a LocalDateTime object. Returns null if parsing fails.
 */
private fun parseLessonDate(timeSlot: String): LocalDateTime? {
  return try {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
    LocalDateTime.parse(timeSlot, formatter)
  } catch (e: Exception) {
    Log.e("RequestedLessonsScreen", "Error parsing date: $timeSlot", e)
    null
  }
}
