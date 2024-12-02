package com.github.se.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AvailabilityGrid(
    schedule: List<List<Int>>,
    onScheduleChange: (List<List<Int>>) -> Unit,
    modifier: Modifier = Modifier
) {
  val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
  val hours = (8..19).map { "$it h" }

  Column(modifier = modifier.testTag("availabilityGrid")) {
    // Buttons Row
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          // Select All Button
          Button(
              onClick = {
                // Select All: Fill the schedule with '1's
                val updatedSchedule = List(7) { List(24) { 1 } }
                onScheduleChange(updatedSchedule)
              },
              colors =
                  ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Select All",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Select All", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
              }

          // Unselect All Button
          Button(
              onClick = {
                // Unselect All: Fill the schedule with '0's
                val updatedSchedule = List(7) { List(24) { 0 } }
                onScheduleChange(updatedSchedule)
              },
              colors =
                  ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Unselect All",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onSecondary)
                Text(text = "Unselect All", color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.bodyMedium)
              }
        }

    // Days Header Row
    Row(modifier = Modifier.testTag("daysRow")) {
      Spacer(modifier = Modifier.width(44.dp).testTag("daySpacer"))
      days.forEachIndexed { dayIndex, day ->
        Box(
            modifier =
                Modifier.weight(1f)
                    .padding(2.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(8.dp))
                    .padding(2.dp)
                    .testTag("dayBox_$dayIndex")) {
              Text(
                  text = day,
                  style = MaterialTheme.typography.bodySmall,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.testTag("dayText_$dayIndex"))
            }
      }
    }

    // Hours and Slots
    hours.forEachIndexed { hourIndex, hour ->
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.testTag("hourRow_$hourIndex")) {
            Box(
                modifier =
                    Modifier.weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceBright,
                            shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .testTag("hourBox_$hourIndex")) {
                  Text(
                      text = hour,
                      style = MaterialTheme.typography.bodySmall,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.testTag("hourText_$hourIndex"))
                }

            days.forEachIndexed { dayIndex, _ ->
              val isSelected = schedule[dayIndex][hourIndex] == 1

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
                                schedule.map { it.toMutableList() }.toMutableList()
                            updatedSchedule[dayIndex][hourIndex] = if (isSelected) 0 else 1
                            onScheduleChange(updatedSchedule)
                          }
                          .testTag("Slot_${dayIndex}_${hourIndex}"))
            }
          }
    }
  }
}
