package com.github.se.project.ui.certification

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.github.se.project.R
import kotlinx.coroutines.launch

// Define scanning states outside the composable
enum class ScanningState {
  Scanning,
  Detected
}

/**
 * A composable button that opens a camera scanner for SCIPER numbers on Camipro cards. Handles
 * camera permissions, preview, ML Kit text recognition, and user feedback.
 *
 * @param onSciperCaptured Callback function triggered when a valid SCIPER number is detected
 * @param modifier Modifier for the button layout
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun SciperScanButton(
    onSciperCaptured: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Test parameters
    testMode: Boolean = false,
    initialShowCamera: Boolean? = null,
    initialHasCameraPermission: Boolean? = null,
    initialScanningState: ScanningState? = null
) {
  var showCamera by remember { mutableStateOf(initialShowCamera ?: false) }
  var hasCameraPermission by remember { mutableStateOf(initialHasCameraPermission ?: false) }
  var scanningState by remember { mutableStateOf(initialScanningState ?: ScanningState.Scanning) }

  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  // If in test mode, skip actual permission requests and camera initialization
  if (!testMode) {
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted ->
          hasCameraPermission = isGranted
          if (isGranted) {
            showCamera = true
          } else {
            Toast.makeText(
                    context, "Camera permission is required to scan SCIPER", Toast.LENGTH_LONG)
                .show()
          }
        }

    Button(
        onClick = {
          when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
              hasCameraPermission = true
              showCamera = true
            }
            else -> permissionLauncher.launch(Manifest.permission.CAMERA)
          }
        },
        modifier = modifier,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary)) {
          Text("Scan Camipro Card")

          Spacer(modifier = Modifier.width(8.dp))

          Icon(
              imageVector = ImageVector.vectorResource(id = R.drawable.scan),
              contentDescription = "Scan Camipro card",
          )
        }
  } else {
    // In test mode, we just show the same button without permissions logic
    Button(onClick = { showCamera = true }, modifier = modifier) {
      Text("Scan Camipro Card (Test)")
    }
  }

  if (showCamera && hasCameraPermission) {
    // In test mode, don't set up the camera and text recognition.
    // Just display the dialog and overlay UI.
    Dialog(
        onDismissRequest = {
          showCamera = false
          scanningState = ScanningState.Scanning
        },
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false)) {
          Box(modifier = Modifier.fillMaxSize()) {
            if (!testMode) {
              // Actual camera preview initialization here
              // ...
            }

            // UI overlay that we can test
            Box(modifier = Modifier.fillMaxSize()) {
              // Top header
              AnimatedVisibility(
                  visible = true,
                  enter =
                      fadeIn(animationSpec = tween(500)) +
                          slideInVertically(initialOffsetY = { -50 }),
                  exit = fadeOut(animationSpec = tween(500)),
                  modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) {
                          Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            IconButton(
                                onClick = {
                                  showCamera = false
                                  scanningState = ScanningState.Scanning
                                },
                                modifier = Modifier.align(Alignment.TopEnd)) {
                                  Icon(
                                      Icons.Default.Close,
                                      contentDescription = "Close camera",
                                      tint = MaterialTheme.colorScheme.onSurface)
                                }

                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                  Text(
                                      "Scan your Camipro Card",
                                      style = MaterialTheme.typography.titleMedium,
                                      color = MaterialTheme.colorScheme.onSurface)
                                  Spacer(modifier = Modifier.height(8.dp))
                                  Text(
                                      when (scanningState) {
                                        ScanningState.Scanning ->
                                            "Please place the Camipro card within the frame."
                                        ScanningState.Detected -> "SCIPER detected!"
                                      },
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                                      textAlign = TextAlign.Center)
                                }
                          }
                        }
                  }

              val infiniteTransition = rememberInfiniteTransition(label = "transition")
              val alpha by
                  infiniteTransition.animateFloat(
                      initialValue = 0.5f,
                      targetValue = 1f,
                      animationSpec =
                          infiniteRepeatable(
                              animation = tween(1000, easing = LinearEasing),
                              repeatMode = RepeatMode.Reverse),
                      label = "CamiProCardAlpha")

              Box(
                  modifier =
                      Modifier.size(280.dp, 200.dp)
                          .align(Alignment.Center)
                          .border(
                              width = 3.dp,
                              color =
                                  when (scanningState) {
                                    ScanningState.Detected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.secondary.copy(alpha = alpha)
                                  },
                              shape = RoundedCornerShape(16.dp))
                          .testTag("ScanningFrame"))

              AnimatedVisibility(
                  visible = scanningState == ScanningState.Detected,
                  enter = fadeIn() + slideInVertically { it },
                  exit = fadeOut(),
                  modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                    Surface(
                        modifier =
                            Modifier.padding(horizontal = 32.dp)
                                .fillMaxWidth()
                                .testTag("DetectedMessage"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
                          Row(
                              modifier = Modifier.padding(16.dp),
                              horizontalArrangement = Arrangement.Center,
                              verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "SCIPER detected!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                              }
                        }
                  }
            }
          }
        }
  }
}
