package com.github.se.project.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),  // Use the theme's background color
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg),  // Replace with your SVG path
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Box to position logo in top left
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Logo in top-left
            Image(
                painter = painterResource(id = R.drawable.logobrand),  // Replace with your logo path
                contentDescription = "Brand logo",
                modifier = Modifier
                    .size(200.dp)  // Adjust size as needed
                    .padding(16.dp)  // Padding from top and left
                    .align(Alignment.TopStart)  // Align logo to top-left
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            // HorizontalPager for PNG image scrolling
            HorizontalPager(
                state = pagerState,
                count = 3,  // Number of images to scroll through
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = when (page) {
                            0 -> R.drawable.home1  // Replace with your actual PNG resources
                            1 -> R.drawable.home2
                            else -> R.drawable.home3
                        }),
                        contentDescription = null,
                        modifier = Modifier
                            .height(250.dp)  // Limit the image height
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        contentScale = ContentScale.Fit  // Use Fit to ensure the image is not stretched
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (page) {
                            0 -> "Simplify learning and teaching with instant connections to the university community."
                            1 -> "Get help or share your expertise – whether you're a student or a tutor, Pocket Tutor works for both."
                            else -> "Pocket Tutor connects university students and tutors for quick, effective learning support."
                        },
                        style = Typography.bodyLarge,  // Apply the text style from the theme
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,  // Use color from theme
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                PaginationDot(isActive = pagerState.currentPage == 0)
                Spacer(modifier = Modifier.width(8.dp))
                PaginationDot(isActive = pagerState.currentPage == 1)
                Spacer(modifier = Modifier.width(8.dp))
                PaginationDot(isActive = pagerState.currentPage == 2)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Google Sign-in Button
            Button(
                onClick = onSignInClick,
                shape = Shapes.medium,  // Apply the custom shape from the theme
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp)
                    .clip(Shapes.medium),
                colors = ButtonColors(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),  // Google logo
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google", style = Typography.labelMedium)  // Apply button text style
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions
            Text(
                text = "By clicking 'Continue', you agree to the Terms of Service and Privacy Policy, and consent to the use of cookies related to PocketTutor.",
                style = Typography.bodySmall,  // Use bodySmall style from the theme
                color = MaterialTheme.colorScheme.onBackground,  // Use color from theme
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PaginationDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(Shapes.small)  // Apply custom shape
            .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    )
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(onSignInClick = {})
}
