package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.AvailabilityGrid
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTutorSchedule(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val profile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

  var currentSchedule by remember { mutableStateOf(profile.schedule) }

  Scaffold(
      topBar = {
        Text(
            text = "${profile.firstName}, show us your availabilities",
            modifier = Modifier.padding(16.dp).testTag("welcomeText"),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Start)
      },
      content = { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("availabilityScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              item {
                Text(
                    text =
                        "Finish your account creation by selecting the time slots you're available during the week:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp).testTag("InstructionsText"))
              }

              item {
                AvailabilityGrid(
                    schedule = currentSchedule,
                    onScheduleChange = { updatedSchedule -> currentSchedule = updatedSchedule },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .testTag("availabilityGrid"))
              }

              item { Spacer(modifier = Modifier.height(16.dp)) }
            }
      },
      bottomBar = {
        Button(
            modifier = Modifier.fillMaxWidth().padding(14.dp).testTag("FindStudentButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = {
              listProfilesViewModel.updateProfile(profile.copy(schedule = currentSchedule))
              navigationActions.navigateTo(Screen.HOME)
            }) {
              Text(text = "Let's find a student!", fontSize = 16.sp)
            }
      })
}
