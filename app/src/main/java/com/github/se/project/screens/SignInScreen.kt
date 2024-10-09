package com.github.se.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(onSignInClick: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Welcome to Pocket Tutor")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSignInClick) { Text(text = "Sign in with Google") }
      }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
  SignInScreen(onSignInClick = {})
}
