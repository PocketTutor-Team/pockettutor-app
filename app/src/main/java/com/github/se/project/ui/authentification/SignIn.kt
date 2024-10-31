package com.github.se.project.ui.authentification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.ui.theme.Shapes
import com.github.se.project.ui.theme.Typography
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SignInScreen(onSignInClick: () -> Unit = {}) {
  val pagerState = rememberPagerState()

  Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    // Background image covering the whole screen
    Image(
        painter = painterResource(id = R.drawable.bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds)

    // Logo at the top
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 16.dp) // Adjust padding as needed
                .align(Alignment.TopCenter)) {
          Image(
              painter = painterResource(id = R.drawable.logobrand),
              contentDescription = "Brand logo",
              modifier = Modifier.size(150.dp).padding(horizontal = 8.dp).testTag("logo"))
        }

    // Center content for images and text
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
          HorizontalPager(
              state = pagerState,
              count = 3,
              modifier = Modifier.height(300.dp).fillMaxWidth().testTag("images")) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                      Image(
                          painter =
                              painterResource(
                                  id =
                                      when (page) {
                                        0 -> R.drawable.home1
                                        1 -> R.drawable.home2
                                        else -> R.drawable.home3
                                      }),
                          contentDescription = null,
                          modifier =
                              Modifier.height(200.dp).fillMaxWidth().padding(horizontal = 16.dp),
                          contentScale = ContentScale.Fit)

                      Spacer(modifier = Modifier.height(16.dp))

                      Text(
                          text =
                              when (page) {
                                0 ->
                                    "Welcome to Pocket Tutor, Simplify learning and teaching with instant connections to the university community."
                                1 ->
                                    "Get help or share your expertise – whether you're a student or a tutor, Pocket Tutor works for both."
                                else ->
                                    "Pocket Tutor connects university students and tutors for quick, effective learning support."
                              },
                          style = Typography.bodyLarge,
                          textAlign = TextAlign.Center,
                          color = MaterialTheme.colorScheme.onBackground,
                          modifier = Modifier.fillMaxWidth().padding(horizontal = 42.dp))
                    }
              }

          // Pagination Dots
          Row(
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.testTag("dots").fillMaxWidth()) {
                PaginationDot(isActive = pagerState.currentPage == 0)
                Spacer(modifier = Modifier.width(8.dp))
                PaginationDot(isActive = pagerState.currentPage == 1)
                Spacer(modifier = Modifier.width(8.dp))
                PaginationDot(isActive = pagerState.currentPage == 2)
              }
        }

    // Bottom content for Google sign-in button and Terms
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp) // Adjust bottom padding as needed
        ) {
          Button(
              onClick = onSignInClick,
              shape = Shapes.medium,
              modifier =
                  Modifier.fillMaxWidth(0.8f)
                      .height(48.dp)
                      .clip(Shapes.medium)
                      .testTag("loginButton")) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google", style = Typography.labelMedium)
              }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
              text =
                  "By clicking 'Continue', you agree to the Terms of Service and Privacy Policy, and consent to the use of cookies related to PocketTutor.",
              style = Typography.bodySmall,
              modifier = Modifier.testTag("terms").padding(horizontal = 32.dp),
              textAlign = TextAlign.Center)
        }
  }
}

@Composable
fun PaginationDot(isActive: Boolean) {
  Box(
      modifier =
          Modifier.size(8.dp)
              .clip(Shapes.small) // Apply custom shape
              .background(
                  if (isActive) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurface))
}
