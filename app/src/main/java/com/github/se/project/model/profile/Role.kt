package com.github.se.project.model.profile

/** Enum classes representing the role of a user. */
enum class Role {
  STUDENT,
  TUTOR,
  UNKNOWN
  // Note: Use "UNKNOWN" for base value before the user selects a role
  // but we should not create a profile without a role STUDENT or TUTOR
}
