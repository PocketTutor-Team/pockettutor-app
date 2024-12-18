package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.project.ui.certification.SciperScanButton

@Composable
fun EpflVerificationDialog(isVerified: Boolean, onDismiss: () -> Unit, onVerify: (String) -> Unit) {
  var sciper by remember { mutableStateOf("") }

  AlertDialog(
      onDismissRequest = onDismiss,
      icon = {
        Icon(
            if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint =
                if (isVerified) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface)
      },
      title = {
        // Add a test tag to the title if needed
        Text(
            text = if (isVerified) "EPFL Verified" else "EPFL Verification Required",
            modifier = Modifier.testTag("epflVerificationTitle"))
      },
      text = {
        if (isVerified) {
          Text("Your EPFL profile has been verified")
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Verify your EPFL profile to build trust with students")
            OutlinedTextField(
                value = sciper,
                onValueChange = { if (it.length <= 6) sciper = it },
                label = { Text("SCIPER Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth())

            SciperScanButton(onSciperCaptured = { sciper = it }, modifier = Modifier.fillMaxWidth())
          }
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              if (!isVerified && sciper.length == 6) {
                onVerify(sciper)
              }
              onDismiss()
            },
            enabled = isVerified || sciper.length == 6,
            modifier = Modifier.testTag("epflVerificationConfirmButton")) {
              Text(if (isVerified) "Close" else "Verify")
            }
      },
      dismissButton = {
        if (!isVerified) {
          TextButton(
              onClick = onDismiss, modifier = Modifier.testTag("epflVerificationDismissButton")) {
                Text("Later")
              }
        }
      })
}
