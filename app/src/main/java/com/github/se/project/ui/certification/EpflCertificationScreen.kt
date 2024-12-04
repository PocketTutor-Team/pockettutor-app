package com.github.se.project.ui.certification

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.CertificationViewModel.VerificationState
import com.github.se.project.ui.navigation.NavigationActions

/**
 * Main screen for EPFL profile certification. This screen allows users to verify their EPFL
 * identity either by manually entering their SCIPER number or scanning their Camipro card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpflCertificationScreen(
    viewModel: CertificationViewModel,
    navigationActions: NavigationActions
) {
  // State management
  var sciper by remember { mutableStateOf("") }
  var showConfirmDialog by remember { mutableStateOf(false) }
  val verificationState by viewModel.verificationState.collectAsState()
  val context = LocalContext.current

  // Check if profile is already verified
  LaunchedEffect(Unit) {
    if (viewModel.isProfileVerified()) {
      // Show already verified message and navigate back
      Toast.makeText(context, "Profile is already verified with EPFL", Toast.LENGTH_LONG).show()
      navigationActions.goBack()
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("EPFL Verification") },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(Icons.Default.ArrowBack, "Back")
                  }
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              // Header section with explanation
              CertificationHeader()

              // Input section
              CertificationInput(
                  sciper = sciper,
                  onSciperChange = { sciper = it },
                  onScanClick = { /* Camera functionality */},
                  onVerifyClick = { showConfirmDialog = true },
                  isLoading = verificationState is VerificationState.Loading)

              // Status section showing verification result
              AnimatedVisibility(
                  visible = verificationState !is VerificationState.Idle,
                  enter = fadeIn(),
                  exit = fadeOut()) {
                    VerificationStatus(verificationState)
                  }
            }

        // Confirmation dialog
        if (showConfirmDialog) {
          CertificationConfirmDialog(
              sciper = sciper,
              onConfirm = {
                viewModel.verifySciperNumber(sciper)
                showConfirmDialog = false
              },
              onDismiss = { showConfirmDialog = false })
        }
      }
}

@Composable
private fun CertificationHeader() {
  Column(modifier = Modifier.padding(vertical = 16.dp)) {
    Text(
        "EPFL Profile Verification",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp))
    Text(
        "Link your EPFL profile to unlock additional features and verify your academic status. " +
            "You can either scan your Camipro card or enter your SCIPER number manually.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun CertificationInput(
    sciper: String,
    onSciperChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onVerifyClick: () -> Unit,
    isLoading: Boolean
) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // SCIPER input field
        OutlinedTextField(
            value = sciper,
            onValueChange = { if (it.length <= 6) onSciperChange(it) },
            label = { Text("SCIPER Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("sciperInput"))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Scan button
              Button(
                  onClick = onScanClick,
                  modifier = Modifier.weight(1f),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.secondaryContainer,
                          contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                    Icon(Icons.Default.Search, null, Modifier.padding(end = 8.dp))
                    Text("Scan Card")
                  }

              // Verify button
              Button(
                  onClick = onVerifyClick,
                  enabled = sciper.length == 6 && !isLoading,
                  modifier = Modifier.weight(1f)) {
                    if (isLoading) {
                      CircularProgressIndicator(
                          color = MaterialTheme.colorScheme.onPrimary,
                          modifier = Modifier.size(20.dp))
                    } else {
                      Text("Verify")
                    }
                  }
            }
      }
}

@Composable
private fun VerificationStatus(state: VerificationState) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  when (state) {
                    is VerificationState.Success -> MaterialTheme.colorScheme.primaryContainer
                    is VerificationState.Error -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                  })) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              when (state) {
                is VerificationState.Success -> {
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "✓ EPFL Profile Verified", style = MaterialTheme.typography.titleMedium)
                      }
                  Text("${state.result.firstName} ${state.result.lastName}")
                  Text("${state.result.section} - ${state.result.academicLevel}")
                }
                is VerificationState.Error -> {
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                      }
                }
                else -> {} // Handle other states if needed
              }
            }
      }
}

@Composable
private fun CertificationConfirmDialog(
    sciper: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Confirm SCIPER Verification") },
      text = {
        Text(
            "We'll fetch your EPFL profile data using SCIPER $sciper. " +
                "This will update your profile information. Continue?",
            textAlign = TextAlign.Center)
      },
      confirmButton = { Button(onClick = onConfirm) { Text("Confirm") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
