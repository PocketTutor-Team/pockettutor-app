package com.github.se.project.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme

// Primary colors
val PrimaryLight = Color(0xFF4A3071)   // Dark violet
val PrimaryLightVariant = Color(0xFFAF80E3)  // Light violet

// Secondary colors
val SecondaryLight = Color(0xFFEF9CDF)  // Pink
val SecondaryLightVariant = Color(0xFFD178A9)  // Dark pink

// Background and surface
val BackgroundLight = Color(0xFFFFFFFF)  // White
val SurfaceLight = Color(0xFFF7F7F7)  // Very light gray for surfaces (cards, dialogs)

// Text colors
val OnPrimaryLight = Color(0xFFFFFFFF)  // White text on violet background
val OnSecondaryLight = Color(0xFF000000)  // Black text on pink background
val OnBackgroundLight = Color(0xFF333333)  // Dark gray text on white background

// Status colors
val ErrorLight = Color(0xFFD32F2F)  // Red for errors
val SuccessLight = Color(0xFF388E3C)  // Green for success

// Disabled elements
val DisabledLight = Color(0xFFBDBDBD)  // Light gray for disabled elements
val DisabledOnBackgroundLight = Color(0xFF757575)  // Disabled text on light background

// Accent and interaction
val AccentLight = Color(0xFFFFC107)  // Yellow for accents
val HoverLight = Color(0xFFF5F5F5)  // Very light gray for hover states

// Dark Theme

// Primary colors
val PrimaryDark = Color(0xFFAF80E3)  // Light violet (in dark mode, use a lighter version of violet)
val PrimaryDarkVariant = Color(0xFF4A3071)  // Dark violet

// Secondary colors
val SecondaryDark = Color(0xFFEF9CDF)  // Dark pink
val SecondaryDarkVariant = Color(0xFFF1A0D4)  // Light pink

// Background and surface
val BackgroundDark = Color(0xFF121212)  // Very dark gray/black for the background
val SurfaceDark = Color(0xFF1E1E1E)  // Dark gray for surfaces

// Text colors
val OnPrimaryDark = Color(0xFF000000)  // Black text on light violet background
val OnSecondaryDark = Color(0xFFFFFFFF)  // White text on pink background
val OnBackgroundDark = Color(0xFFFFFFFF)  // White text on dark background

// Status colors
val ErrorDark = Color(0xFFCF6679)  // Light red for errors (adapted for dark mode)
val SuccessDark = Color(0xFF81C784)  // Light green for success

// Disabled elements
val DisabledDark = Color(0xFF424242)  // Dark gray for disabled elements
val DisabledOnBackgroundDark = Color(0xFF757575)  // Disabled text on dark background

// Accent and interaction
val AccentDark = Color(0xFFFFD54F)  // Light yellow accent for interactive elements
val HoverDark = Color(0xFF2E2E2E)  // Gray for hover state in dark mode
