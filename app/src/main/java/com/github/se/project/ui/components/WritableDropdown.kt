package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex

/**
 * A composable function that implements a writable dropdown, where the user can type to filter
 * through a list of choices. Once a choice is selected, it updates the value and closes the
 * dropdown.
 *
 * @param label The label for the text field displayed above the dropdown.
 * @param placeholder A placeholder text shown inside the text field when it is empty.
 * @param value The current value of the text field.
 * @param onValueChange A callback function that is triggered when the value of the text field
 *   changes.
 * @param choices A list of strings representing the available choices to be displayed in the
 *   dropdown.
 */
@Composable
fun WritableDropdown(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    choices: List<String>
) {
  // State to track if the dropdown menu is expanded or collapsed based on user input.
  var expanded by remember { mutableStateOf(false) }

  Box {
    /**
     * Outlined text field for user input. The dropdown expands when the user types, and contracts
     * when the input is empty.
     */
    OutlinedTextField(
        value = value, // Displays the current value in the text field.
        onValueChange = {
          // Updates the text value and toggles dropdown visibility.
          onValueChange(it)
          expanded = it.isNotEmpty() // Expands the dropdown when there is input.
        },
        label = { Text(label) }, // Displays the label above the text field.
        placeholder = { Text(placeholder) }, // Displays a placeholder when the input is empty.
        modifier =
            Modifier.testTag("dropdown").fillMaxWidth(), // Full-width and test tag for testing.
        singleLine = true) // Ensures the text field is a single line.

    /**
     * Dropdown menu that appears below the text field when the user starts typing. It shows only
     * the filtered choices based on the user's input.
     */
    DropdownMenu(
        expanded = expanded, // Shows or hides the dropdown based on the state.
        onDismissRequest = { expanded = false }, // Dismisses the dropdown when tapped outside.
        modifier =
            Modifier.fillMaxWidth()
                .zIndex(1f), // Full-width and ensures dropdown appears above other elements.
        properties = PopupProperties(focusable = false)) { // Makes the dropdown not focusable.
          // Filters the choices based on the user's input and displays matching items.
          choices
              .filter { v ->
                (v.startsWith(value) || v.lowercase().startsWith(value))
              } // Filters choices.
              .forEach { v ->
                /**
                 * A dropdown menu item for each matching choice. When clicked, it updates the value
                 * of the text field and collapses the dropdown.
                 */
                DropdownMenuItem(
                    text = { Text(v) }, // Displays the choice.
                    modifier = Modifier.testTag("item_$v"), // Test tag for item-based testing.
                    onClick = {
                      onValueChange(v) // Updates the value when a choice is selected.
                      expanded = false // Closes the dropdown after selection.
                    })
              }
        }
  }
}
