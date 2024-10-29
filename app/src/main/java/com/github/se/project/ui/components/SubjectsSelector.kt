package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.project.model.profile.Subject

@Composable
fun SubjectsSelector(
    selectedSubjects: MutableList<Subject>,
) {
  val expandedSubjectDropdown = remember { mutableStateOf(false) }

  val subjects = Subject.entries.toTypedArray()
  Box(modifier = Modifier.fillMaxWidth()) {
    Button(
        onClick = { expandedSubjectDropdown.value = !expandedSubjectDropdown.value },
        modifier = Modifier.fillMaxWidth().testTag("subjectButton")) {
          Text("Select Subjects")
        }
    DropdownMenu(
        expanded = expandedSubjectDropdown.value,
        onDismissRequest = { expandedSubjectDropdown.value = false },
        modifier = Modifier.fillMaxWidth()) {
          subjects
              .filter { it != Subject.NONE }
              .forEach { subject ->
                val isSelected = selectedSubjects.contains(subject)
                DropdownMenuItem(
                    text = {
                      Row {
                        if (isSelected) {
                          Icon(Icons.Filled.Check, contentDescription = null)
                        }
                        Text(subject.name.lowercase())
                      }
                    },
                    onClick = {
                      if (isSelected) {
                        selectedSubjects.remove(subject)
                      } else {
                        selectedSubjects.add(subject)
                      }
                    },
                    modifier = Modifier.testTag("dropdown${subject.name}"))
              }
        }
  }
}
