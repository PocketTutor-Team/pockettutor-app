package com.github.se.project.utils

import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
      tutorProfile: Profile
      // studentProfile: Profile
  ): Int {
    // weights
    val W1 = 0.35 // subject Match
    val W2 = 0.25 // schedule Compatibility
    // val W3 = 0.15 // academic Level Compatibility
    val W4 = 0.25 // language Match
    val W5 = 0.10 // price Compatibility
    val W6 = 0.05 // distance Proximity

    // Feature Calculations
    val X1 = computeSubjectMatch(lesson, tutorProfile)
    val X2 = computeScheduleMatch(lesson, tutorProfile)
    // val X3 = computeAcademicLevelCompatibility(tutorProfile, studentProfile)
    val X4 = computeLanguageMatch(lesson, tutorProfile)
    val X5 = computePriceCompatibility(lesson, tutorProfile)
    val X6 = computeDistanceProximity(lesson, tutorProfile)

    // Compute the total score
    val score = (W1 * X1 + W2 * X2 + W4 * X4 + W5 * X5 + W6 * X6) * 100

    return score.toInt()
  }

  private fun computeSubjectMatch(lesson: Lesson, tutorProfile: Profile): Double {
    return if (tutorProfile.subjects.contains(lesson.subject)) 1.0 else 0.0
  }

  private fun computeScheduleMatch(lesson: Lesson, tutorProfile: Profile): Double {
    val lessonDateTime = parseLessonDate(lesson.timeSlot) ?: return 0.0

    val dayOfWeekIndex = (lessonDateTime.dayOfWeek.value % 7)
    val hourIndex = lessonDateTime.hour - 8 // schedule starts at 8h

    if (hourIndex !in 0..11) {
      return 0.0
    }

    val tutorIsAvailable =
        tutorProfile.schedule.getOrNull(dayOfWeekIndex - 1)?.getOrNull(hourIndex) == 1

    return if (tutorIsAvailable) 1.0 else 0.0
  }

  // TODO: use this function but might be computationally costly to compare with student profile
  private fun computeAcademicLevelCompatibility(
      tutorProfile: Profile,
      studentProfile: Profile
  ): Double {
    val tutorLevelIndex = tutorProfile.academicLevel.ordinal
    val studentLevelIndex = studentProfile.academicLevel.ordinal

    val levelDifference = tutorLevelIndex - studentLevelIndex

    // score is better if the tutor is at least 2 academic level above the student
    val score =
        when {
          levelDifference >= 2 -> 1.0
          levelDifference == 1 -> 0.75
          levelDifference == 0 -> 0.5
          else -> 0.0 // tutor is below student's level
        }

    return score
  }

  private fun computeLanguageMatch(lesson: Lesson, tutorProfile: Profile): Double {
    return if (lesson.languages.any { it in tutorProfile.languages }) {
      1.0 // at least one language matches
    } else {
      0.0 // no matching languages
    }
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

  private fun parseLessonDate(timeSlot: String): LocalDateTime? {
    return try {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
      LocalDateTime.parse(timeSlot, formatter)
    } catch (e: Exception) {
      null
    }
  }
}
