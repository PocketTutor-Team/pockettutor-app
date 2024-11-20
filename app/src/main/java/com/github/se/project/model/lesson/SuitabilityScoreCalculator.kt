package com.github.se.project.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Profile

object SuitabilityScoreCalculator {

  /**
   * Calculates the suitability score of a lesson for a given tutor profile.
   *
   * @param lesson The lesson to evaluate.
   * @param tutorProfile The tutor's profile.
   * @param studentProfile The student's profile.
   * @return The suitability score as an integer between 0 and 100.
   */
  fun calculateSuitabilityScore(
      lesson: Lesson,
      tutorProfile: Profile,
      studentProfile: Profile?
  ): Int {
    // weights
    val W1 = 0.25 // schedule Compatibility
    val W2 = 0.35 // academic Level Compatibility
    val W3 = 0.25 // price Compatibility
    val W4 = 0.15 // distance Proximity

    // Feature Calculations
    val X1 = computeScheduleMatch(lesson, tutorProfile)
    val X2 = computeAcademicLevelCompatibility(tutorProfile, studentProfile)
    val X3 = computePriceCompatibility(lesson, tutorProfile)
    val X4 = computeDistanceProximity(lesson, tutorProfile)

    // Compute the total score
    val score = (W1 * X1 + W2 * X2 + W3 * X3 + W4 * X4) * 100
    return score.toInt()
  }

  private fun computeScheduleMatch(lesson: Lesson, tutorProfile: Profile): Double {
    val lessonDateTime = lesson.parseLessonDate() ?: return 0.0

    val dayOfWeekIndex = (lessonDateTime.dayOfWeek.value % 7)
    val hourIndex = lessonDateTime.hour - 8 // schedule starts at 8h

    if (hourIndex !in 0..11) {
      return 0.0
    }

    val tutorIsAvailable = tutorProfile.schedule[dayOfWeekIndex - 1][hourIndex] == 1

    return if (tutorIsAvailable) 1.0 else 0.0
  }

  // TODO: this function might be computationally costly to retrieve every student profile.
  // I would like to ask the coaches
  private fun computeAcademicLevelCompatibility(
      tutorProfile: Profile,
      studentProfile: Profile?
  ): Double {
    if (studentProfile == null) {
      return 0.0 // return a default score if studentProfile is null
    }

    val tutorLevelIndex = tutorProfile.academicLevel.ordinal
    val studentLevelIndex = studentProfile.academicLevel.ordinal
    val levelDifference = tutorLevelIndex - studentLevelIndex

    if (levelDifference < 0) return 0.0

    val maxLevelDiff = AcademicLevel.entries.size - studentLevelIndex
    // Ensure the level difference is within bounds [0, maxLevelDiff]
    val normalizedDifference =
        levelDifference.coerceIn(0, maxLevelDiff).toDouble() / maxLevelDiff.toDouble()

    val score = 0.3 + 0.7 * normalizedDifference

    return score
  }

  private fun computePriceCompatibility(lesson: Lesson, tutorProfile: Profile): Double {
    val tutorPrice = tutorProfile.price.toDouble()
    val minPrice = lesson.minPrice
    val maxPrice = lesson.maxPrice

    val score =
        when {
          tutorPrice in minPrice..maxPrice -> 1.0 // perfect match
          tutorPrice < minPrice ->
              1.0 - (minPrice - tutorPrice) / minPrice // linear decrease below range
          tutorPrice > maxPrice ->
              1.0 - (tutorPrice - maxPrice) / maxPrice // linear decrease above range
          else -> 0.0 // should not happen
        }.coerceIn(0.0, 1.0) // makes sure score is between 0 and 1

    return score
  }

  private fun computeDistanceProximity(lesson: Lesson, tutorProfile: Profile): Double {
    // TODO: implement this compatibility function using tutor location
    return 1.0
  }

  fun computeSubjectMatch(lesson: Lesson, tutorProfile: Profile): Boolean {
    return tutorProfile.subjects.contains(lesson.subject)
  }

  fun computeLanguageMatch(lesson: Lesson, tutorProfile: Profile): Boolean {
    return lesson.languages.any { it in tutorProfile.languages }
  }

  /**
   * Computes a smooth color based on the suitability score.
   *
   * @param score The suitability score (0 to 100).
   * @return A Color object representing the score.
   */
  fun getColorForScore(score: Int, isDarkTheme: Boolean): Color {
    val normalizedScore = (score / 100f).coerceIn(0f, 1f)

    // Define the colors for 0%, 50%, and 100%
    val red = Color(0xFFFF0000)
    val orange = Color(0xFFFFA500)
    val green =
        if (isDarkTheme) Color(0xFF00FF00) else Color(0xFF009F00) // Darker green for light mode

    return when {
      normalizedScore <= 0.5f ->
          lerp(red, orange, normalizedScore * 2) // Interpolate between red and orange
      else ->
          lerp(orange, green, (normalizedScore - 0.5f) * 2) // Interpolate between orange and green
    }
  }
}
