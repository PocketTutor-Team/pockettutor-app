package com.github.se.project.model.profile

import com.google.firebase.Timestamp

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

  /**
   * Converts this `Rating` object into a map representation.
   *
   * @return A `Map<String, Any>` containing the fields of the `Rating` object.
   */
  fun toMap(): Map<String, Any> {
    return mapOf(
        "averageRating" to averageRating,
        "totalRatings" to totalRatings,
        "comments" to
            comments.map { comment ->
              mapOf(
                  "grade" to comment.grade,
                  "name" to comment.raterUid,
                  "date" to comment.date,
                  "comment" to comment.comment)
            })
  }

  companion object {
    /**
     * Converts a Firestore `DocumentSnapshot` into a `Rating` object.
     *
     * @param ratingMap A Map representing the fields for constructing a `Rating` object.
     * @return A `Rating` object constructed from the provided map.
     */
    fun mapToRating(ratingMap: Map<String, Any?>?): Rating {
      if (ratingMap == null) return Rating()

      val averageRating = ratingMap["averageRating"] as? Double ?: 0.0
      val totalRatings = (ratingMap["totalRatings"] as? Long)?.toInt() ?: 0
      val comments =
          (ratingMap["comments"] as? List<Map<String, Any?>>)?.map { commentMap ->
            Comment(
                grade = (commentMap["grade"] as? Long)?.toInt() ?: 0,
                raterUid = commentMap["raterUid"] as? String ?: "",
                date = commentMap["date"] as? Timestamp ?: Timestamp.now(),
                comment = commentMap["comment"] as? String ?: "")
          } ?: emptyList()

      return Rating(averageRating, totalRatings, comments.toMutableList())
    }
  }
}
