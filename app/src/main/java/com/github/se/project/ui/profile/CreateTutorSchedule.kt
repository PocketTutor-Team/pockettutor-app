package com.github.se.project.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun AvailabilityScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val profile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

  var currentSchedule = profile.schedule

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(text = "Availability") },
            navigationIcon = {
              IconButton(onClick = { /* Handle navigation */}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back")
              }
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "${profile.firstName}, show us your availabilities",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 8.dp))

              Text(
                  text =
                      "Finish your account creation by selecting the time slots you're available during the week:",
                  fontSize = 16.sp,
                  modifier = Modifier.padding(bottom = 8.dp))

              // Display the availability grid
              AvailabilityGrid(
                  schedule = profile.schedule,
                  onScheduleChange = { updatedSchedule -> currentSchedule = updatedSchedule })

              Spacer(modifier = Modifier.weight(1f))

              Button(
                  onClick = { /* Add your logic for finding a student */
                    // Update the profile with the new schedule
                    listProfilesViewModel.updateProfile(profile.copy(schedule = currentSchedule))

                    // Navigate to the home screen
                    navigationActions.navigateTo(Screen.HOME)
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(Color(0xFF9B59B6))) {
                    Text(text = "Let's find a student!", color = Color.White)
                  }
            }
      }
}

@Composable
fun AvailabilityGrid(schedule: List<List<Int>>, onScheduleChange: (List<List<Int>>) -> Unit) {
  val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  val hours = (8..19).map { "$it h" }

  // We need to copy the schedule locally
  var selectedSlots by remember { mutableStateOf(schedule) }

  // Grid Layout
  Column {
    Row {
      Spacer(modifier = Modifier.width(44.dp)) // Placeholder to align with time slots
      days.forEach { day ->
        Box(
            modifier =
                Modifier.weight(1f)
                    .padding(4.dp)
                    .background(
                        Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)) // Box for day labels
                    .padding(4.dp)) {
              Text(
                  text = day,
                  fontSize = 14.sp,
                  textAlign = TextAlign.Center // Center text inside the box
                  )
            }
      }
    }

    hours.forEachIndexed { hourIndex, hour ->
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier.offset(y = (-18).dp)
                    .padding(4.dp)
                    .background(
                        Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp)) // Box for hour labels
                    .padding(4.dp)
                    .weight(1f)) {
              Text(
                  text = hour,
                  fontSize = 14.sp,
                  textAlign = TextAlign.Center // Center text inside the box
                  )
            }

        days.forEachIndexed { dayIndex, _ ->
          val isSelected = selectedSlots[dayIndex][hourIndex] == 1

          Box(
              modifier =
                  Modifier.weight(1f)
                      .aspectRatio(1f)
                      .padding(4.dp)
                      .background(
                          if (isSelected) Color(0xFF9B59B6) else Color.LightGray,
                          shape = RoundedCornerShape(8.dp))
                      .clickable {
                        val updatedSchedule =
                            selectedSlots.toMutableList().map { it.toMutableList() }

                        updatedSchedule[dayIndex][hourIndex] =
                            if (isSelected) 0 else 1 // Toggle the selection

                        selectedSlots = updatedSchedule
                        onScheduleChange(updatedSchedule)
                      })
        }
      }
    }
  }
}
