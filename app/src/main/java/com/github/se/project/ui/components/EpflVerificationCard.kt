package com.github.se.project.ui.components

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.certification.CertificationViewModel.VerificationState
import com.github.se.project.ui.certification.SciperScanButton

@ExperimentalGetImage
@Composable
fun EpflVerificationCard(
    sciper: String,
    onSciperChange: (String) -> Unit,
    verificationState: VerificationState,
    onVerifyClick: (String) -> Unit,
    onResetVerification: () -> Unit,
    modifier: Modifier = Modifier
) {
  Card(
      modifier = modifier.fillMaxWidth().animateContentSize(),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  when (verificationState) {
                    is VerificationState.Success ->
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    is VerificationState.Error ->
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                  })) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector =
                            when (verificationState) {
                              is VerificationState.Success -> Icons.Default.CheckCircle
                              is VerificationState.Error -> Icons.Default.Close
                              else -> ImageVector.vectorResource(id = R.drawable.certified)
                            },
                        contentDescription = null,
                        tint =
                            when (verificationState) {
                              is VerificationState.Success -> MaterialTheme.colorScheme.primary
                              is VerificationState.Error -> MaterialTheme.colorScheme.error
                              else -> MaterialTheme.colorScheme.primary
                            })
                    Text("EPFL Verification", style = MaterialTheme.typography.titleMedium)
                  }

              AnimatedVisibility(
                  visible = verificationState !is VerificationState.Success,
                  enter = fadeIn() + expandVertically(),
                  exit = fadeOut() + shrinkVertically()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                      Text(
                          "Verify your EPFL status by scanning your Camipro card or entering your SCIPER number.",
                          style = MaterialTheme.typography.bodyMedium)

                      OutlinedTextField(
                          value = sciper,
                          onValueChange = { if (it.length <= 6) onSciperChange(it) },
                          label = { Text("SCIPER Number") },
                          modifier = Modifier.fillMaxWidth(),
                          singleLine = true,
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                          isError = verificationState is VerificationState.Error,
                          supportingText = {
                            when (verificationState) {
                              is VerificationState.Error ->
                                  Text(
                                      (verificationState as VerificationState.Error).message,
                                      color = MaterialTheme.colorScheme.error)
                              else -> null
                            }
                          },
                          trailingIcon = {
                            if (sciper.length == 6) {
                              IconButton(onClick = { onVerifyClick(sciper) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Verify",
                                    tint =
                                        when (verificationState) {
                                          is VerificationState.Error ->
                                              MaterialTheme.colorScheme.error
                                          else -> MaterialTheme.colorScheme.primary
                                        })
                              }
                            }
                          })

                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            Text(
                                "or",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                          }

                      SciperScanButton(
                          onSciperCaptured = onVerifyClick, modifier = Modifier.fillMaxWidth())
                    }
                  }

              AnimatedVisibility(
                  visible = verificationState is VerificationState.Success,
                  enter = fadeIn() + expandVertically(),
                  exit = fadeOut() + shrinkVertically()) {
                    when (val state = verificationState) {
                      is VerificationState.Success -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                              Column {
                                Text(
                                    "Verified as ${state.result.firstName} ${state.result.lastName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondary)
                                Text(
                                    "${state.result.section} - ${state.result.academicLevel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =
                                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f))
                              }
                              IconButton(onClick = onResetVerification) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset verification",
                                    tint = MaterialTheme.colorScheme.primary)
                              }
                            }
                      }
                      else -> {}
                    }
                  }

              if (verificationState is VerificationState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
              }
            }
      }
}
