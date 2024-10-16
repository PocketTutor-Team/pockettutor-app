package com.github.se.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light Color Scheme
private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight, // Primary color
        onPrimary = OnPrimaryLight, // Text color on primary
        primaryContainer = PrimaryLightVariant, // Primary container color
        onPrimaryContainer = OnPrimaryLight, // Text color on primary container
        inversePrimary = PrimaryDark, // Inverse primary color
        secondary = SecondaryLight, // Secondary color
        onSecondary = OnSecondaryLight, // Text color on secondary
        secondaryContainer = SecondaryLightVariant, // Secondary container color
        onSecondaryContainer = OnSecondaryLight, // Text color on secondary container
        tertiary = TertiaryLight, // Tertiary color for accents
        onTertiary = OnTertiaryLight, // Text color on tertiary
        tertiaryContainer = TertiaryLightVariant, // Tertiary container color
        onTertiaryContainer = OnTertiaryLight, // Text color on tertiary container
        background = BackgroundLight, // Background color
        onBackground = OnBackgroundLight, // Text color on background
        surface = SurfaceLight, // Surface color (for cards, dialogs)
        onSurface = OnSurfaceLight, // Text color on surface
        surfaceVariant = ElevatedSurfaceLight, // Variant for elevated elements
        onSurfaceVariant = OnSurfaceLight, // Text on surface variant
        surfaceTint = PrimaryLight, // Tint for elevated surfaces
        inverseSurface = InverseSurfaceLight, // Inverse surface for special components
        inverseOnSurface = OnInverseSurfaceLight, // Text on inverse surface
        error = ErrorLight, // Error color
        onError = OnErrorLight, // Text on error
        errorContainer = ErrorContainerLight, // Container for error
        onErrorContainer = OnErrorContainerLight, // Text on error container
        outline = OutlineLight, // Outline color
        outlineVariant = OutlineVariantLight, // Outline variant
        scrim = ScrimLight, // Scrim for modals and backgrounds
        surfaceBright = SurfaceBrightLight, // Brighter surface color
        surfaceContainer = SurfaceContainerLight, // Container color for surfaces
        surfaceContainerHigh = SurfaceContainerHighLight, // High contrast container
        surfaceContainerHighest = SurfaceContainerHighestLight, // Highest contrast container
        surfaceContainerLow = SurfaceContainerLowLight, // Low contrast container
        surfaceContainerLowest = SurfaceContainerLowestLight, // Lowest contrast container
        surfaceDim = SurfaceDimLight // Dimmed surface color
        )

// Dark Color Scheme
private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark, // Primary color
        onPrimary = OnPrimaryDark, // Text color on primary
        primaryContainer = PrimaryDarkVariant, // Primary container color
        onPrimaryContainer = OnPrimaryDark, // Text color on primary container
        inversePrimary = PrimaryLight, // Inverse primary color
        secondary = SecondaryDark, // Secondary color
        onSecondary = OnSecondaryDark, // Text color on secondary
        secondaryContainer = SecondaryDarkVariant, // Secondary container color
        onSecondaryContainer = OnSecondaryDark, // Text color on secondary container
        tertiary = TertiaryDark, // Tertiary color for accents
        onTertiary = OnTertiaryDark, // Text color on tertiary
        tertiaryContainer = TertiaryDarkVariant, // Tertiary container color
        onTertiaryContainer = OnTertiaryDark, // Text color on tertiary container
        background = BackgroundDark, // Background color
        onBackground = OnBackgroundDark, // Text color on background
        surface = SurfaceDark, // Surface color (for cards, dialogs)
        onSurface = OnSurfaceDark, // Text color on surface
        surfaceVariant = ElevatedSurfaceDark, // Variant for elevated elements
        onSurfaceVariant = OnSurfaceDark, // Text on surface variant
        surfaceTint = PrimaryDark, // Tint for elevated surfaces
        inverseSurface = InverseSurfaceDark, // Inverse surface for special components
        inverseOnSurface = OnInverseSurfaceDark, // Text on inverse surface
        error = ErrorDark, // Error color
        onError = OnErrorDark, // Text on error
        errorContainer = ErrorContainerDark, // Container for error
        onErrorContainer = OnErrorContainerDark, // Text on error container
        outline = OutlineDark, // Outline color
        outlineVariant = OutlineVariantDark, // Outline variant
        scrim = ScrimDark, // Scrim for modals and backgrounds
        surfaceBright = SurfaceBrightDark, // Brighter surface color
        surfaceContainer = SurfaceContainerDark, // Container color for surfaces
        surfaceContainerHigh = SurfaceContainerHighDark, // High contrast container
        surfaceContainerHighest = SurfaceContainerHighestDark, // Highest contrast container
        surfaceContainerLow = SurfaceContainerLowDark, // Low contrast container
        surfaceContainerLowest = SurfaceContainerLowestDark, // Lowest contrast container
        surfaceDim = SurfaceDimDark // Dimmed surface color
        )

@Composable
fun SampleAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography, // Defined in Typography.kt
      shapes = Shapes, // Defined in Shapes.kt
      content = content)
}
