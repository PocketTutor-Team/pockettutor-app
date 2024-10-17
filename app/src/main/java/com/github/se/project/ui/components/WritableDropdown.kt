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

@Composable
fun WritableDropdown(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    choices: List<String>
) {
  var expanded by remember { mutableStateOf(false) }

  Box {
    OutlinedTextField(
        value = value,
        onValueChange = {
          onValueChange(it)
          expanded = it.isNotEmpty()
          // Note: this avoid displaying the dropdown menu with all choices
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth().zIndex(1f),
        properties = PopupProperties(focusable = false)) {
          // Display only the choices that corresponds to the user input
          choices
              .filter { v -> (v.startsWith(value) || v.lowercase().startsWith(value)) }
              .forEach { v ->
                DropdownMenuItem(
                    text = { Text(v) },
                    modifier = Modifier.testTag("item_$v"),
                    onClick = {
                      onValueChange(v)
                      expanded = false
                    })
              }
        }
  }
}
