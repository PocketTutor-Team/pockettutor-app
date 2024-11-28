package com.github.se.project.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.se.project.model.lesson.LessonStatus

data class SectionInfo(
    val title: String,
    val status: LessonStatus,
    val icon: ImageVector,
    val tutorEmpty: Boolean = false
)
