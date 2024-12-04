package com.github.se.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.Section

@Composable
fun SectionSelector(section: MutableState<Section?>, enabled: Boolean = true) {
  val expandedDropdown = remember { mutableStateOf(false) }

  Box {
    Text(
        text = section.value?.name ?: "Section",
        modifier =
            Modifier.fillMaxWidth()
                .clickable(enabled = enabled) { expandedDropdown.value = !expandedDropdown.value }
                .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                .border(1.dp, if(enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                .padding(16.dp)
                .testTag("sectionDropdown")
                .alpha(if (enabled) 1f else 0.3f), // Visual feedback for disabled state
        style = MaterialTheme.typography.bodyLarge)

    DropdownMenu(
        expanded = expandedDropdown.value && enabled,
        onDismissRequest = { expandedDropdown.value = false },
        modifier = Modifier.fillMaxWidth()) {
          Section.entries.forEach { s ->
            DropdownMenuItem(
                text = { Text(s.name, style = MaterialTheme.typography.bodyMedium) },
                onClick = {
                  section.value = s
                  expandedDropdown.value = false
                },
                modifier = Modifier.testTag("sectionDropdownItem-${s.name}"))
          }
        }
  }
}
