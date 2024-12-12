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

/**
 * Composable function for the SignIn screen that handles the UI of the authentication page.
 *
 * @param onSignInClick Lambda function that will be executed when the Google sign-in button is
 *   clicked. Default is an empty lambda.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SignInScreen(onSignInClick: () -> Unit = {}) {
  // Remember pager state to control the pager for onboarding images
  val pagerState = rememberPagerState()

  // Box is used to stack all elements on top of each other
  Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    // Background image covering the entire screen
    Image(
        painter = painterResource(id = R.drawable.bg), // Background image
        contentDescription = null, // No description as it’s decorative
        modifier = Modifier.fillMaxSize(), // Fill the screen
        contentScale = ContentScale.FillBounds) // Stretch to cover bounds

    // Logo at the top center
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 16.dp) // Adjust top padding to place logo
                .align(Alignment.TopCenter)) {
          Image(
              painter = painterResource(id = R.drawable.logobrand), // Logo image
              contentDescription = "Brand logo", // Content description for accessibility
              modifier =
                  Modifier.size(150.dp)
                      .padding(horizontal = 8.dp)
                      .testTag("logo")) // Apply size and padding
    }

    // Centered content with images and introductory text
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center elements horizontally
        verticalArrangement = Arrangement.Center, // Center elements vertically
        modifier = Modifier.fillMaxSize()) {

          // Horizontal pager to display multiple onboarding images
          HorizontalPager(
              state = pagerState,
              count = 3, // Total number of pages
              modifier = Modifier.height(300.dp).fillMaxWidth().testTag("images")) { page ->
                // Display different content based on the current page
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                      // Display the images in the pager
                      Image(
                          painter =
                              painterResource(
                                  id =
                                      when (page) {
                                        0 -> R.drawable.home1 // Page 1 image
                                        1 -> R.drawable.home2 // Page 2 image
                                        else -> R.drawable.home3 // Page 3 image
                                      }),
                          contentDescription = null, // No description for images
                          modifier =
                              Modifier.height(200.dp).fillMaxWidth().padding(horizontal = 16.dp),
                          contentScale = ContentScale.Fit) // Fit the image within the container

                      Spacer(
                          modifier =
                              Modifier.height(
                                  16.dp)) // Spacer to create space between the image and text

                      // Displaying text for each page
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
                          style = Typography.bodyLarge, // Apply typography style
                          textAlign = TextAlign.Center, // Center-align the text
                          color =
                              MaterialTheme.colorScheme
                                  .onBackground, // Set text color based on theme
                          modifier =
                              Modifier.fillMaxWidth().padding(horizontal = 42.dp)) // Adjust padding
                }
              }

          // Row for pagination dots to indicate the current page in the pager
          Row(
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.testTag("dots").fillMaxWidth()) {
                // Each dot represents a page in the pager
                PaginationDot(isActive = pagerState.currentPage == 0)
                Spacer(modifier = Modifier.width(8.dp)) // Add space between dots
                PaginationDot(isActive = pagerState.currentPage == 1)
                Spacer(modifier = Modifier.width(8.dp))
                PaginationDot(isActive = pagerState.currentPage == 2)
              }
        }

    // Bottom content containing the Google sign-in button and terms message
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally, // Align content to the center horizontally
        modifier =
            Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter) // Align to the bottom of the screen
                .padding(bottom = 16.dp)) { // Adjust bottom padding

          // Google sign-in button
          Button(
              onClick = onSignInClick, // Trigger the onSignInClick action on button press
              shape = Shapes.medium, // Apply custom shape
              modifier =
                  Modifier.fillMaxWidth(0.8f)
                      .height(48.dp)
                      .clip(Shapes.medium) // Clip button to desired shape
                      .testTag("loginButton")) { // Apply testTag for UI testing
                // Google logo inside the button
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null, // No description for logo
                    modifier = Modifier.size(24.dp)) // Size of the logo
                Spacer(modifier = Modifier.width(8.dp)) // Space between logo and text
                Text(text = "Continue with Google", style = Typography.labelMedium) // Button text
          }

          Spacer(modifier = Modifier.height(16.dp)) // Spacer between button and text

          // Terms of service message
          Text(
              text =
                  "By clicking 'Continue', you agree to the Terms of Service and Privacy Policy, and consent to the use of cookies related to PocketTutor.",
              style = Typography.bodySmall, // Apply smaller text style
              modifier = Modifier.testTag("terms").padding(horizontal = 32.dp), // Apply padding
              textAlign = TextAlign.Center) // Center-align the text
    }
  }
}

/**
 * Composable function to display a pagination dot indicating the current page.
 *
 * @param isActive Boolean value that determines whether the dot is active (i.e., the current page
 *   in the pager).
 */
@Composable
fun PaginationDot(isActive: Boolean) {
  Box(
      modifier =
          Modifier.size(8.dp) // Size of the dot
              .clip(Shapes.small) // Apply custom shape for the dot
              .background(
                  if (isActive) MaterialTheme.colorScheme.primary // Active dot color
                  else MaterialTheme.colorScheme.onSurface)) // Inactive dot color
}
