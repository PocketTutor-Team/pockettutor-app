import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_TUTOR
import com.github.se.project.ui.navigation.NavigationActions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun LessonsRequestedScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {

  // Update requested lessons each time we arrive on this screen
  LaunchedEffect(Unit) { lessonViewModel.getAllRequestedLessons(onComplete = {}) }

  val currentProfile by listProfilesViewModel.currentProfile.collectAsState()
  val requestedLessons by lessonViewModel.requestedLessons.collectAsState()

  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var selectedTab by remember { mutableStateOf(LIST_TOP_LEVEL_DESTINATIONS_TUTOR.first().route) }

  // Filter and sort lessons by the selected date, if a date is selected
  val filteredLessons =
      requestedLessons
          .filter { lesson ->
            selectedDate == null || parseLessonDate(lesson.timeSlot)?.toLocalDate() == selectedDate
          }
          .sortedBy { parseLessonDate(it.timeSlot) }

  Scaffold(
      topBar = {
        LessonsRequestedTopBar(
            selectedDate = selectedDate, onDateSelected = { newDate -> selectedDate = newDate })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route ->
              selectedTab = route.route
              navigationActions.navigateTo(route)
            },
            tabList = LIST_TOP_LEVEL_DESTINATIONS_TUTOR,
            selectedItem = navigationActions.currentRoute(),
        )
      }) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
          if (filteredLessons.isEmpty()) {
            Text(
                text = "No lesson available for that date :(",
                modifier = Modifier.align(Alignment.Center).testTag("noLessonsMessage"))
          } else {
            DisplayLessons(
                modifier =
                    Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .testTag("lessonsList"),
                lessons = filteredLessons,
                isTutor = (currentProfile?.role == Role.TUTOR))
          }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsRequestedTopBar(selectedDate: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
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

  TopAppBar(
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.testTag("topBar")) {
          Text(text = "Lessons Requested", modifier = Modifier.testTag("screenTitle"))
          Spacer(modifier = Modifier.width(8.dp))
        }
      },
      actions = {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            modifier =
                Modifier.clickable { datePickerDialog.show() }
                    .testTag("datePicker") // Added test tag
            ) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedDate?.format(dateFormatter) ?: "-/-")
                  }
            }

        IconButton(
            onClick = { /* TODO: Additional filter options */},
            modifier = Modifier.testTag("filterButton")) {
              Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Filter")
            }
      })
}

// function to return LocalDateTime for sorting by date and time
fun parseLessonDate(timeSlot: String): LocalDateTime? {
  return try {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
    LocalDateTime.parse(timeSlot, formatter)
  } catch (e: Exception) {
    Log.e("parseLessonDate", "Error parsing date: $timeSlot", e)
    null
  }
}
