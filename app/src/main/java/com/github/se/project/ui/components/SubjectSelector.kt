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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.github.se.project.R
import com.github.se.project.model.profile.Subject

@Composable
fun SubjectSelector(
    selectedSubject: MutableState<Subject>? = null,
    selectedSubjects: MutableList<Subject>? = null,
    multipleSelection: Boolean = false
) {
  val expandedDropdown = remember { mutableStateOf(false) }
  val subjects = Subject.entries.filter { it != Subject.NONE }

  Box(modifier = Modifier.fillMaxWidth()) {
    // Button to open dropdown
    Button(
        onClick = { expandedDropdown.value = !expandedDropdown.value },
        modifier = Modifier.fillMaxWidth().testTag("subjectButton"),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
          Text(
              if (multipleSelection) {
                if (selectedSubjects.isNullOrEmpty()) stringResource(R.string.select_subject)
                else selectedSubjects.joinToString(", ") { it.name.lowercase().replace("_", " ") }
              } else {
                if (selectedSubject?.value == Subject.NONE) stringResource(R.string.select_subjects)
                else selectedSubject?.value?.name?.lowercase()?.replace("_", " ").orEmpty()
              },
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onPrimary)
        }

    // Dropdown menu
    DropdownMenu(
        expanded = expandedDropdown.value,
        onDismissRequest = { expandedDropdown.value = false },
        modifier = Modifier.fillMaxWidth().zIndex(1f),
        properties = PopupProperties(focusable = true)) {
          subjects.forEach { subject ->
            val isSelected =
                if (multipleSelection) {
                  selectedSubjects?.contains(subject) == true
                } else {
                  selectedSubject?.value == subject
                }
            DropdownMenuItem(
                text = {
                  Row {
                    Text(
                        subject.name.replace('_', ' ').lowercase(),
                        style = MaterialTheme.typography.bodyMedium)
                    if (isSelected) {
                      Icon(
                          Icons.Filled.Check,
                          contentDescription = null,
                          modifier = Modifier.testTag("${subject.name}Checkmark"))
                    }
                  }
                },
                onClick = {
                  if (multipleSelection && selectedSubjects != null) {
                    if (isSelected) {
                      selectedSubjects.remove(subject)
                    } else {
                      selectedSubjects.add(subject)
                    }
                  } else if (!multipleSelection && selectedSubject != null) {
                    selectedSubject.value = subject
                  }
                  if (!multipleSelection) {
                    expandedDropdown.value = false
                  }
                },
                modifier = Modifier.testTag("dropdown${subject.name}"))
          }
        }
  }
}
