import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTutorSchedule(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory)
) {
    val profile = listProfilesViewModel.currentProfile.collectAsState().value
        ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

    var currentSchedule = profile.schedule

    Scaffold(
        topBar = {
            Text(
                text = "${profile.firstName}, show us your availabilities",
                modifier = Modifier.padding(16.dp).testTag("welcomeText"),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(paddingValues)
                    .testTag("availabilityScreen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Finish your account creation by selecting the time slots you're available during the week:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("instructionText")
                )

                AvailabilityGrid(
                    schedule = currentSchedule,
                    onScheduleChange = { updatedSchedule -> currentSchedule = updatedSchedule },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .testTag("confirmButton"),
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        listProfilesViewModel.updateProfile(profile.copy(schedule = currentSchedule))
                        navigationActions.navigateTo(Screen.HOME)
                    }
                ) {
                    Text(text = "Let's find a student!", fontSize = 16.sp)
                }
            }
        }
    )
}

@Composable
fun AvailabilityGrid(schedule: List<List<Int>>, onScheduleChange: (List<List<Int>>) -> Unit, modifier: Modifier = Modifier) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val hours = (8..19).map { "$it h" }

    var selectedSlots by remember { mutableStateOf(schedule) }

    Column(modifier = modifier) {
        Row {
            Spacer(modifier = Modifier.width(44.dp))
            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }


        hours.forEachIndexed { hourIndex, hour ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = hour,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                days.forEachIndexed { dayIndex, _ ->
                    val isSelected = selectedSlots[dayIndex][hourIndex] == 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surfaceBright,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                val updatedSchedule = selectedSlots.toMutableList().map { it.toMutableList() }
                                updatedSchedule[dayIndex][hourIndex] = if (isSelected) 0 else 1
                                selectedSlots = updatedSchedule
                                onScheduleChange(updatedSchedule)
                            }
                    )
                }
            }
        }
    }
}


