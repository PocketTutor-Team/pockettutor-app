package com.github.se.project.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.theme.SampleAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(profile: Profile, onProfileUpdate: (Profile) -> Unit) {
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
                  onScheduleChange = { updatedSchedule ->
                    val updatedProfile = profile.copy(schedule = updatedSchedule)
                    onProfileUpdate(updatedProfile)
                  })

              Spacer(modifier = Modifier.weight(1f))

              Button(
                  onClick = { /* Add your logic for finding a student */
                    profile.schedule.forEachIndexed { dayIndex, daySchedule ->
                      println("Day $dayIndex: ${daySchedule.joinToString()}")
                    }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  val initialProfile =
      Profile(
          uid = "12345",
          firstName = "John",
          lastName = "Doe",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          email = "john.doe@example.com",
          schedule = List(7) { List(12) { 1 } } // Initial empty schedule
          )
  SampleAppTheme {
    AvailabilityScreen(profile = initialProfile, onProfileUpdate = { /* Handle profile update */})
  }
}
