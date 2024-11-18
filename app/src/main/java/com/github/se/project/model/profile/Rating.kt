package com.github.se.project.model.profile

data class Rating(
    var averageRating: Double = 0.0, // average rating (1 to 5 stars)
    var totalRatings: Int = 0, // total number of ratings
    val comments: MutableList<Comment> = mutableListOf() // list of comments
) {
  /**
   * Adds a new rating and updates the average.
   *
   * @param comment the Comment object representing the new rating.
   */
  fun addRating(comment: Comment) {
    comments.add(comment)
    totalRatings += 1
    averageRating = comments.sumOf { it.grade.toDouble() } / totalRatings
  }
}
