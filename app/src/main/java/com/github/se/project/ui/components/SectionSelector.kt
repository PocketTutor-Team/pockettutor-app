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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.github.se.project.model.profile.Section

@Composable
fun SectionSelector(section: MutableState<Section?> = mutableStateOf(null)) {
  val expandedDropdown = remember { mutableStateOf(false) }
  Box {
    Text(
        text = section.value?.name ?: "Section", // Directly access the value
        modifier =
            Modifier.fillMaxWidth()
                .clickable { expandedDropdown.value = !expandedDropdown.value }
                .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                .testTag("sectionDropdown")
                .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge)

    DropdownMenu(
        expanded = expandedDropdown.value,
        onDismissRequest = { expandedDropdown.value = false },
        modifier = Modifier.fillMaxWidth().zIndex(1f),
        properties = PopupProperties(focusable = true)) {
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
