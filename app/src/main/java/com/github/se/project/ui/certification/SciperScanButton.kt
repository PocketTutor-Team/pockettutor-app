package com.github.se.project.ui.certification

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.github.se.project.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import kotlinx.coroutines.delay
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
fun SciperScanButton(onSciperCaptured: (String) -> Unit, modifier: Modifier = Modifier) {
  // State management for camera and scanning
  var showCamera by remember { mutableStateOf(false) }
  var hasCameraPermission by remember { mutableStateOf(false) }

  // Define scanning states
  var scanningState by remember { mutableStateOf(ScanningState.Scanning) }

  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  // Permission handling
  val permissionLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
          isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
          showCamera = true
        } else {
          Toast.makeText(context, "Camera permission is required to scan SCIPER", Toast.LENGTH_LONG)
              .show()
        }
      }

  // Main scan button
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

  // Camera scanning dialog
  if (showCamera && hasCameraPermission) {
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
          // Remember the PreviewView and the camera executor
          val previewView = remember { PreviewView(context) }
          val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
          var isProcessingImage by remember { mutableStateOf(false) }

          // Dispose of the executor when the dialog is dismissed
          DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }

          Box(modifier = Modifier.fillMaxSize()) {
            // Camera Preview
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

            // Camera setup
            LaunchedEffect(Unit) {
              val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
              val cameraProvider = cameraProviderFuture.get()
              val preview =
                  Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                  }

              val imageAnalysis =
                  ImageAnalysis.Builder()
                      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                      .build()

              val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

              imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (!isProcessingImage && scanningState == ScanningState.Scanning) {
                  isProcessingImage = true
                  val mediaImage = imageProxy.image

                  if (mediaImage != null) {
                    val image =
                        InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    textRecognizer
                        .process(image)
                        .addOnSuccessListener { visionText ->
                          val sciperPattern = Regex("\\b\\d{6}\\b")
                          val matchResult = sciperPattern.find(visionText.text)

                          matchResult?.value?.let { sciper ->
                            scanningState = ScanningState.Detected
                            scope.launch {
                              delay(2000) // Wait for 2 second
                              onSciperCaptured(sciper)
                              showCamera = false
                              scanningState = ScanningState.Scanning
                            }
                          }
                        }
                        .addOnFailureListener { e ->
                          Log.e("SciperScan", "Text recognition failed", e)
                        }
                        .addOnCompleteListener {
                          imageProxy.close()
                          isProcessingImage = false
                        }
                  } else {
                    imageProxy.close()
                    isProcessingImage = false
                  }
                } else {
                  imageProxy.close()
                }
              }

              try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
              } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
              }
            }

            // UI Overlay with Enhanced Top Bar
            Box(modifier = Modifier.fillMaxSize()) {
              // Top header with fade-in animation
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
                            // Close button aligned to the top end
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

                            // Title and scanning state messages centered
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

              // Scanning Frame with Subtle Animation
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
                              shape = RoundedCornerShape(16.dp)))

              // Status Indicator with Animated Visibility
              AnimatedVisibility(
                  visible = scanningState == ScanningState.Detected,
                  enter = fadeIn() + slideInVertically { it },
                  exit = fadeOut(),
                  modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
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
                                    modifier =
                                        Modifier.size(24.dp)
                                            .animateEnterExit(enter = fadeIn(), exit = fadeOut()))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "SCIPER detected!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier =
                                        Modifier.animateEnterExit(
                                            enter = fadeIn(), exit = fadeOut()))
                              }
                        }
                  }
            }
          }
        }
  }
}
