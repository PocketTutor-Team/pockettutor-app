package com.github.se.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

  var selectedSlots by remember { mutableStateOf(schedule) }

  Column(modifier = modifier) {
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

        days.forEachIndexed { dayIndex, _ ->
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
                      })
        }
      }
    }
  }
}
