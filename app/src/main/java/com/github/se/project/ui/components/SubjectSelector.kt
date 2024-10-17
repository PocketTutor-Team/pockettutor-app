package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.github.se.project.model.profile.Subject

@Composable
fun SubjectSelector(
    selectedSubject: MutableState<Subject>,
) {
  val expandedDropdown = remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth()) {
    Button(
        onClick = { expandedDropdown.value = true },
        modifier = Modifier.fillMaxWidth().testTag("subjectButton"),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
          Text(
              if (selectedSubject.value == Subject.NONE) "Select Subject"
              else selectedSubject.value.name.lowercase(),
              style = MaterialTheme.typography.labelMedium)
        }

    DropdownMenu(
        expanded = expandedDropdown.value,
        onDismissRequest = { expandedDropdown.value = false },
        modifier = Modifier.fillMaxWidth().zIndex(1f),
        properties = PopupProperties(focusable = true)) {
          Subject.entries
              .filter { it != Subject.NONE }
              .forEach { subject ->
                DropdownMenuItem(
                    text = {
                      Row {
                        Text(subject.name.lowercase(), style = MaterialTheme.typography.bodyMedium)

                        if (selectedSubject.value == subject) {
                          Icon(Icons.Filled.Check, contentDescription = null)
                        }
                      }
                    },
                    onClick = {
                      selectedSubject.value = subject
                      expandedDropdown.value = false
                    },
                    modifier = Modifier.testTag("dropdownItem-${subject.name}"))
              }
        }
  }
}
