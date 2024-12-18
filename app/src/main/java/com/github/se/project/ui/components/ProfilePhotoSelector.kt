package com.github.se.project.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.github.se.project.R
import java.io.File
import kotlinx.coroutines.launch

@SuppressLint("Recycle")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePhotoSelector(
    currentPhotoUrl: Uri? = null,
    onLocalPhotoSelected: (Uri?) -> Unit, // Callback for local URI
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var currentPhotoUrl by remember { mutableStateOf(currentPhotoUrl) }
  var showBottomSheet by remember { mutableStateOf(false) }
  var permissionGranted by remember { mutableStateOf(false) }
  var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var previewUri by remember { mutableStateOf<Uri?>(null) } // Preview only local photo

  // Camera launcher
  val cameraLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success
        ->
        if (success && tempPhotoUri != null) {
          val fileSize = context.contentResolver.openInputStream(tempPhotoUri!!)?.available()
          if (fileSize != null && fileSize > 5 * 1024 * 1024) {
            Toast.makeText(context, "File exceeds 5 MB size limit", Toast.LENGTH_SHORT).show()
          } else {
            previewUri = tempPhotoUri
            onLocalPhotoSelected(tempPhotoUri)
          }
        }
      }

  // Permission launcher
  val cameraPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
          showBottomSheet = true
        }
      }

  // Check initial permission
  LaunchedEffect(Unit) {
    permissionGranted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
  }

  // Gallery launcher
  val galleryLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        uri?.let {
          previewUri = it // Local preview
          onLocalPhotoSelected(it)
        }
      }

  // Function to create a temporary file for the camera photo
  fun createTempPhotoUri(): Uri? {
    return try {
      val photoFile =
          File.createTempFile("temp_profile_photo", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
          }
      FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
    } catch (e: Exception) {
      Toast.makeText(context, "Failed to create file for camera", Toast.LENGTH_SHORT).show()
      null
    }
  }

  // Function to handle photo delete
  fun handlePhotoDelete() {
    previewUri = null
    tempPhotoUri = null
    currentPhotoUrl = null
    onLocalPhotoSelected(null)
    showBottomSheet = false
  }

  // UI
  Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
        modifier =
            Modifier.size(120.dp)
                .clip(CircleShape)
                .clickable(
                    onClick = {
                      if (!permissionGranted) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                      } else {
                        showBottomSheet = true
                      }
                      showBottomSheet = true
                    })
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center) {
          val imageUri = previewUri ?: currentPhotoUrl
          if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Profile Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
          } else {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.add_picture),
                contentDescription = "Add Photo",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(40.dp))
          }
        }
  }

  if (showBottomSheet) {
    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
      Column(modifier = Modifier.padding(16.dp)) {
        PhotoOptionButton(
            icon = ImageVector.vectorResource(id = R.drawable.camera),
            text = "Take Photo",
            onClick = {
              if (permissionGranted) {
                val uri = createTempPhotoUri()
                tempPhotoUri = uri
                uri?.let { cameraLauncher.launch(it) }
                showBottomSheet = false
              } else {
                Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
              }
            })
        PhotoOptionButton(
            icon = ImageVector.vectorResource(id = R.drawable.gallery),
            text = "Choose from Gallery",
            onClick = {
              galleryLauncher.launch("image/*")
              showBottomSheet = false
            })
        if (previewUri != null || currentPhotoUrl != null) {
          PhotoOptionButton(
              icon = Icons.Default.Delete,
              text = "Remove Photo",
              onClick = { handlePhotoDelete() },
              isDestructive = true)
        }
      }
    }
  }
}

@Composable
private fun PhotoOptionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
  Surface(
      modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
      shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(
              imageVector = icon,
              contentDescription = null,
              tint =
                  if (isDestructive) MaterialTheme.colorScheme.error
                  else MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
              text = text,
              color =
                  if (isDestructive) MaterialTheme.colorScheme.error
                  else MaterialTheme.colorScheme.onSurface)
        }
      }
}
