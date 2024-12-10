package com.github.se.project.model.lesson

import com.github.se.project.model.lesson.SuitabilityScoreCalculator.computeAcademicLevelCompatibility
import com.github.se.project.model.profile.Profile

object TutorRecommender {

  /**
   * Recommends tutors based on academic level compatibility, section match, and tutor rating.
   *
   * @param studentProfile The profile of the student who posted the lesson.
   * @param availableTutors A list of potential tutor profiles.
   * @param lessonViewModel The lesson view model.
   * @return A sorted list of tutors ranked by suitability.
   */
  fun recommendTutorsForLesson(
      studentProfile: Profile,
      availableTutors: List<Profile>,
      lessonViewModel: LessonViewModel
  ): List<Profile> {

    val completedLessons = lessonViewModel.currentUserLessons.value
    val ratedLessons =
        completedLessons.filter { it.status == LessonStatus.COMPLETED && it.rating != null }
    val averageRating = ratedLessons.mapNotNull { it.rating?.grade }.average()

    // Weights
    val W1 = 0.35 // Academic Level Compatibility
    val W2 = 0.25 // Section Match
    val W3 = 0.4 // Tutor Rating

    val tutorsWithScores =
        availableTutors.map { tutorProfile ->
          val X1 = computeAcademicLevelCompatibility(tutorProfile, studentProfile)
          val X2 = computeSectionMatch(tutorProfile, studentProfile)
          val X3 =
              if (ratedLessons.isNotEmpty()) computeRatingScore(averageRating)
              else 1.0 // priority to new tutors

          val score = (W1 * X1 + W2 * X2 + W3 * X3) * 100
          tutorProfile to score
        }

    return tutorsWithScores.sortedByDescending { it.second }.map { it.first }
  }

  /** Compute section match: If tutor's section == student's section: 1.0 else 0.0 */
  private fun computeSectionMatch(tutorProfile: Profile, studentProfile: Profile): Double {
    return if (tutorProfile.section == studentProfile.section) 1.0 else 0.0
  }

  /** Compute rating score: If rating is on a 0–5 scale, divide by 5.0 to normalize to 0.0–1.0. */
  private fun computeRatingScore(rating: Double): Double {
    return (rating / 5.0).coerceIn(0.0, 1.0)
  }
}
