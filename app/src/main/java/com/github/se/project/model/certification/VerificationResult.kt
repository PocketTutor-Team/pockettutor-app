// VerificationResult.kt
package com.github.se.project.model.certification

import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Section

sealed class VerificationResult {
  data class Success(
      val firstName: String,
      val lastName: String,
      val academicLevel: AcademicLevel,
      val section: Section
  ) : VerificationResult()

  sealed class Error : VerificationResult() {
    object InvalidSciper : Error()

    object NetworkError : Error()

    object ParsingError : Error()
  }
}
