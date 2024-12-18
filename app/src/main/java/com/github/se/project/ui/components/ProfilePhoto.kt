package com.github.se.project.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun ProfilePhoto(
    photoUri: Uri?,
    size: Dp = 80.dp,
    showPlaceholder: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
  Surface(
      modifier = modifier.size(size).clip(CircleShape).testTag("profilePhoto"),
      color = MaterialTheme.colorScheme.primaryContainer) {
        if (photoUri != null) {
          SubcomposeAsyncImage(
              model = photoUri,
              contentDescription = "Profile Photo",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
              loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  CircularProgressIndicator(modifier = Modifier.size(size / 3))
                }
              },
              error = { if (showPlaceholder) PlaceholderContent(size) })
        } else if (showPlaceholder) {
          PlaceholderContent(size)
        }
      }
}

@Composable
private fun PlaceholderContent(size: Dp) {
  Box(
      modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Placeholder",
            modifier = Modifier.size(size / 2),
            tint = MaterialTheme.colorScheme.onPrimaryContainer)
      }
}
