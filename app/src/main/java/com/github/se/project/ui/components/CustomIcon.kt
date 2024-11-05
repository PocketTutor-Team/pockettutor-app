package com.github.se.project.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomIcon(
    imageVector: ImageVector,
    contentDescription: String?,
) {
  return Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      tint = MaterialTheme.colorScheme.primaryContainer)
}
