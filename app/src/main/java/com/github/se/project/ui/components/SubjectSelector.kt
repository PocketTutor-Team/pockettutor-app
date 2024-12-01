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
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.github.se.project.model.profile.Subject
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

/**
 * A reusable Compose component for selecting one or more subjects from a dropdown menu.
 *
 * @param selectedSubject A [MutableState] object representing the currently selected subject
 *   (used for single-selection mode). Pass `null` for multiple-selection mode.
 * @param selectedSubjects A mutable list representing the selected subjects (used for
 *   multiple-selection mode). Pass `null` for single-selection mode.
 * @param multipleSelection A boolean indicating whether multiple subjects can be selected.
 */
@Composable
fun SubjectSelector(
    selectedSubject: MutableState<Subject>? = null,
    selectedSubjects: MutableList<Subject>? = null,
    multipleSelection: Boolean = false
) {
    // Tracks whether the dropdown menu is expanded or collapsed
    val expandedDropdown = remember { mutableStateOf(false) }

    // Filters the list of subjects to exclude the "NONE" option
    val subjects = Subject.entries.filter { it != Subject.NONE }

    // Scroll state for enabling vertical scrolling in the dropdown
    val scrollState = rememberScrollState()

    // Root container for the SubjectSelector component
    Box(modifier = Modifier.fillMaxWidth()) {
        /**
         * Button to toggle the dropdown menu. Displays the currently selected subject(s) or
         * a placeholder text if none are selected.
         */
        Button(
            onClick = { expandedDropdown.value = !expandedDropdown.value }, // Toggles dropdown state
            modifier = Modifier.fillMaxWidth().testTag("subjectButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                // Displays selected subject(s) or a placeholder
                if (multipleSelection) {
                    if (selectedSubjects.isNullOrEmpty()) "Select Subjects"
                    else selectedSubjects.joinToString(", ") { it.name.lowercase() }
                } else {
                    if (selectedSubject?.value == Subject.NONE) "Select Subject"
                    else selectedSubject?.value?.name?.lowercase().orEmpty()
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        /**
         * Dropdown menu displaying the list of subjects. Supports single or multiple selection
         * modes.
         */
        DropdownMenu(
            expanded = expandedDropdown.value,
            onDismissRequest = { expandedDropdown.value = false }, // Closes the dropdown
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f), // Ensures the dropdown appears above other UI elements
            properties = PopupProperties(focusable = true) // Allows focusable interactions
        ) {
            // Adds vertical scrolling to the dropdown menu
            Box(modifier = Modifier.verticalScroll(scrollState)) {
                // Iterates through the list of subjects to create menu items
                subjects.forEach { subject ->
                    // Determines if the subject is selected (depends on selection mode)
                    val isSelected = if (multipleSelection) {
                        selectedSubjects?.contains(subject) == true
                    } else {
                        selectedSubject?.value == subject
                    }

                    /**
                     * Individual dropdown menu item for each subject.
                     * Allows users to select or deselect a subject.
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
                                        modifier = Modifier.testTag("${subject.name}Checkmark")
                                    )
                                }
                            }
                        },
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
                                expandedDropdown.value = false
                            }
                        },
                        modifier = Modifier.testTag("dropdown${subject.name}")
                    )
                }
            }
        }
    }
}

