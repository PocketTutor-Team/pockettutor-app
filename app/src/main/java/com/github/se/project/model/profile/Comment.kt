package com.github.se.project.model.profile

import com.google.firebase.Timestamp

data class Comment(
    val grade: Int, // rating given (1 to 5 stars)
    val raterUid: String, // uid of the person who rated
    val date: Timestamp, // date of the rating
    val comment: String // actual comment
)
