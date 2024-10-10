package com.github.se.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Color schemes for light and dark modes
private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        secondary = SecondaryDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        onPrimary = OnPrimaryDark,
        onSecondary = OnSecondaryDark,
        onBackground = OnBackgroundDark,
        error = ErrorDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        secondary = SecondaryLight,
        background = BackgroundLight,
        surface = SurfaceLight,
        onPrimary = OnPrimaryLight,
        onSecondary = OnSecondaryLight,
        onBackground = OnBackgroundLight,
        error = ErrorLight,
    )

@Composable
fun SampleAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
      colorScheme = colorScheme, typography = Typography, shapes = Shapes, content = content)
}
