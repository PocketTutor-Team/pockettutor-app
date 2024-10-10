package com.github.se.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define your custom typography
val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,  // Default is 57.sp, customize as needed
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,  // Use for large headings
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,  // Adjust font size for titles
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,  // Standard text size for body text
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,  // Use for labels or buttons
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    )
)

