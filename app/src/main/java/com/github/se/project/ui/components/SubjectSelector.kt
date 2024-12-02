package com.github.se.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.github.se.project.model.profile.Subject

/**
 * A reusable Compose component for selecting one or more subjects from a dropdown menu, with filtering based on the first letter.
 *
 * @param selectedSubject A [MutableState] object representing the currently selected subject (used for single-selection mode). Pass `null` for multiple-selection mode.
 * @param selectedSubjects A mutable list representing the selected subjects (used for multiple-selection mode). Pass `null` for single-selection mode.
 * @param multipleSelection A boolean indicating whether multiple subjects can be selected.
 */
@Composable
fun SubjectSelector(
    selectedSubject: MutableState<Subject>? = null,
    selectedSubjects: MutableList<Subject>? = null,
    multipleSelection: Boolean = false
) {
    // Tracks whether the dropdown menu is expanded or collapsed
    var expanded by remember { mutableStateOf(false) }

    // Holds the current input for filtering the subjects
    var searchQuery by remember { mutableStateOf("") }

    // Filters the list of subjects based on the search query (first letter)
    val filteredSubjects = Subject.entries.filter {
        it != Subject.NONE && it.name.startsWith(searchQuery, ignoreCase = true)
    }

    // Root container for the SubjectSelector component
    Box(modifier = Modifier.fillMaxWidth()) {
        /**
         * OutlinedTextField to allow typing a query and filtering subjects
         */
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                expanded = it.isNotEmpty()  // Show the dropdown only when there is some input
            },
            label = { Text("Select Subject") },
            placeholder = { Text("Start typing to filter subjects") },
            modifier = Modifier.testTag("dropdown").fillMaxWidth(),
            singleLine = true
        )

        /**
         * Dropdown menu displaying the filtered list of subjects.
         */
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Closes the dropdown
            modifier = Modifier.fillMaxWidth().zIndex(1f),
            properties = PopupProperties(focusable = false) // Disable focusable to prevent issues
        ) {
            filteredSubjects.forEach { subject ->
                // Determines if the subject is selected (depends on selection mode)
                val isSelected =
                    if (multipleSelection) {
                        selectedSubjects?.contains(subject) == true
                    } else {
                        selectedSubject?.value == subject
                    }

                /**
                 * Individual dropdown menu item for each subject. Allows users to select or deselect
                 * a subject.
                 */
                DropdownMenuItem(
                    text = {
                        Row {
                            // Displays the subject name
                            Text(subject.name.lowercase(), style = MaterialTheme.typography.bodyMedium)
                            // Displays a checkmark if the subject is selected
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.testTag("${subject.name}Checkmark"))
                            }
                        }
                    },
                    modifier = Modifier.testTag("dropdown${subject.name}"),
                    onClick = {
                        // Updates the selection based on the mode
                        if (multipleSelection && selectedSubjects != null) {
                            if (isSelected) {
                                selectedSubjects.remove(subject) // Deselects the subject
                            } else {
                                selectedSubjects.add(subject) // Selects the subject
                            }
                        } else if (!multipleSelection && selectedSubject != null) {
                            selectedSubject.value = subject // Sets the selected subject
                        }
                        // Closes the dropdown in single-selection mode
                        if (!multipleSelection) {
                            expanded = false
                        }
                    }
                )
            }
        }
    }
}
