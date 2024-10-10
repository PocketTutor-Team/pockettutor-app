package com.github.se.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.screens.SignInScreen
import com.github.se.project.ui.theme.SampleAppTheme
import com.github.se.project.viewmodels.AuthenticationViewModel

class MainActivity : ComponentActivity() {
  private lateinit var authenticationViewModel: AuthenticationViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SampleAppTheme {
        val context = LocalContext.current
        this.authenticationViewModel = viewModel()

        SignInScreen(
            onSignInClick = {
              this.authenticationViewModel.handleGoogleSignIn(
                  context,
                  {
                    // navigate to home screen
                  })
            })
      }
    }
  }
}
