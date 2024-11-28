package com.github.se.project.model.lesson

import com.google.firebase.Timestamp

data class LessonRating(
    val grade: Int = 0,
    val comment: String = "",
    val date: Timestamp = Timestamp.now(),
    val canEdit: Boolean = true // allow modifications for 24 hours
) {
  fun isEditable(): Boolean {
    if (!canEdit) return false
    val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
    return System.currentTimeMillis() - date.toDate().time < twentyFourHoursInMillis
  }
}
