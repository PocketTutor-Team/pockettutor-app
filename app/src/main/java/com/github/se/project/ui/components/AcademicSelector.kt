package com.github.se.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.AcademicLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicSelector(academicLevel: MutableState<AcademicLevel?>, enabled: Boolean = true) {
  val expandedDropdown = remember { mutableStateOf(false) }

  Box {
    Text(
        text = academicLevel.value?.name ?: "Academic Level",
        modifier =
            Modifier.fillMaxWidth()
                .clickable(enabled = enabled) { expandedDropdown.value = !expandedDropdown.value }
                .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                .padding(16.dp)
                .testTag("academicLevelDropdown")
                .alpha(if (enabled) 1f else 0.6f), // Visual feedback for disabled state
        style = MaterialTheme.typography.bodyLarge)

    DropdownMenu(
        expanded = expandedDropdown.value && enabled,
        onDismissRequest = { expandedDropdown.value = false },
        modifier = Modifier.fillMaxWidth()) {
          AcademicLevel.entries.forEach { s ->
            DropdownMenuItem(
                text = { Text(s.name, style = MaterialTheme.typography.bodyMedium) },
                onClick = {
                  academicLevel.value = s
                  expandedDropdown.value = false
                },
                modifier = Modifier.testTag("academicLevelDropdownItem-${s.name}"))
          }
        }
  }
}
