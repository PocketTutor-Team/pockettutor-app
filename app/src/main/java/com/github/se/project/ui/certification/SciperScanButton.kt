// SciperScanButton.kt
package com.github.se.project.ui.certification

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

/** A composable button that handles camera functionality for scanning SCIPER numbers. */
@Composable
fun SciperScanButton(onSciperCaptured: (String) -> Unit, modifier: Modifier = Modifier) {
  var showCamera by remember { mutableStateOf(false) }
  var hasCameraPermission by remember { mutableStateOf(false) }
  val context = LocalContext.current

  // Permission launcher
  val permissionLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
          isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
          showCamera = true
        }
      }

  // Main button
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
      modifier = modifier.testTag("cameraScanButton"),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
              contentColor = MaterialTheme.colorScheme.onPrimary)) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Scan Camipro card",
            modifier = Modifier.padding(end = 8.dp))
        Text("Scan Camipro Card")
      }

  if (showCamera && hasCameraPermission) {
    CameraDialog(
        onDismiss = { showCamera = false },
        onSciperDetected = { sciper ->
          onSciperCaptured(sciper)
          showCamera = false
        })
  }
}

@Composable
private fun CameraDialog(onDismiss: () -> Unit, onSciperDetected: (String) -> Unit) {
  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              dismissOnBackPress = true,
              dismissOnClickOutside = false,
              usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize()) {
          CameraPreview(onSciperDetected = onSciperDetected)

          // Close button
          IconButton(
              onClick = onDismiss,
              modifier =
                  Modifier.align(Alignment.TopEnd).padding(16.dp).testTag("closeCameraButton")) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close camera",
                    tint = MaterialTheme.colorScheme.onSurface)
              }

          // Overlay text
          Text(
              "Position SCIPER number in frame",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.align(Alignment.Center).padding(16.dp))
        }
      }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreview(onSciperDetected: (String) -> Unit) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
  val textRecognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

  DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }

  val previewView = remember {
    PreviewView(context).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE }
  }

  LaunchedEffect(previewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    try {
      val cameraProvider = cameraProviderFuture.get()

      // Unbind all uses cases
      cameraProvider.unbindAll()

      // Create preview use case
      val preview = Preview.Builder().build()
      preview.setSurfaceProvider(previewView.surfaceProvider)

      // Create image analysis use case
      val imageAnalysis =
          ImageAnalysis.Builder()
              .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
              .build()

      imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
          val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

          textRecognizer
              .process(image)
              .addOnSuccessListener { visionText ->
                // Look for SCIPER pattern (6 digits)
                val sciperPattern = Regex("\\b\\d{6}\\b")
                val matchResult = sciperPattern.find(visionText.text)
                matchResult?.value?.let { sciper -> onSciperDetected(sciper) }
              }
              .addOnCompleteListener { imageProxy.close() }
        } else {
          imageProxy.close()
        }
      }

      // Bind use cases to camera
      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
      cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
    } catch (e: Exception) {
      Log.e("CameraPreview", "Camera setup failed", e)
    }
  }

  AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}
