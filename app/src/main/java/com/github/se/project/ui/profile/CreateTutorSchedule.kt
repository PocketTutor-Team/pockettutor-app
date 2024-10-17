package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.*
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


@Composable
fun AvailabilityGrid(
    schedule: List<List<Int>>,
    onScheduleChange: (List<List<Int>>) -> Unit,
    modifier: Modifier = Modifier
) {
  val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  val hours = (8..19).map { "$it h" }

  var selectedSlots by remember { mutableStateOf(schedule) }

  Column(modifier = Modifier.testTag("AvailabilityGrid")) {
    Row {
      Spacer(modifier = Modifier.width(44.dp))
      days.forEach { day ->
        Box(
            modifier =
                Modifier.weight(1f)
                    .padding(2.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(8.dp))
                    .padding(2.dp)) {
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
            modifier =
                Modifier.weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)) {
              Text(
                  text = hour,
                  style = MaterialTheme.typography.bodySmall,
                  textAlign = TextAlign.Center)
            }

        days.forEachIndexed { dayIndex, day ->
          val isSelected = selectedSlots[dayIndex][hourIndex] == 1

          Box(
              modifier =
                  Modifier.weight(1f)
                      .aspectRatio(1f)
                      .padding(4.dp)
                      .background(
                          if (isSelected) MaterialTheme.colorScheme.inverseSurface
                          else MaterialTheme.colorScheme.surfaceBright,
                          shape = RoundedCornerShape(8.dp))
                      .clickable {
                        val updatedSchedule =
                            selectedSlots.toMutableList().map { it.toMutableList() }
                        updatedSchedule[dayIndex][hourIndex] = if (isSelected) 0 else 1
                        selectedSlots = updatedSchedule
                        onScheduleChange(updatedSchedule)
                      }
                      .testTag("Slot_${day}_${hour}"))
        }
      }
    }
  }
}

