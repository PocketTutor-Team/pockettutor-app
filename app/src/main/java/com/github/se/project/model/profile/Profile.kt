package com.github.se.project.model.profile

import java.util.EnumSet

/** Data class representing a user profile. */
data class Profile(
    val uid: String, // Unique string id for the profile
    val firstName: String, // First name of the user
    val lastName: String, // Last name of the user
    val role: Role, // Role of the user
    val section: Section, // Section of the user
    val academicLevel: AcademicLevel, // Academic level of the user
    val email: String, // Email of the user => TODO: link to google sign-in
    val languages: EnumSet<Language> = EnumSet.of(Language.STUDENT),
    val subjects: EnumSet<TutoringSubject> = EnumSet.noneOf(TutoringSubject::class.java),
    val schedule: List<List<Int>> = List(7) { List(12) { 0 } }, // Weekly schedule
    var price: Int = 0,
    // TODO: profile picture
)

/** Enum classes representing the role, section, and academic level of a user. */
enum class Role {
  STUDENT,
  TUTOR,
  UNKNOWN
  // Note: Use "UNKNOWN" for base value before the user selects a role
  // but we should not create a profile without a role STUDENT or TUTOR
}

enum class Section {
  IN,
  SC, // School of Computer and Communication Sciences (IC)
  AR,
  GC,
  SIE, // School of Architecture, Civil and Environmental Engineering (ENAC)
  SGC,
  MA,
  PH, // School of Basic Sciences (SB)
  EL,
  MX,
  GM,
  MT, // School of Engineering (STI)
  SV, // School of Life Sciences (SV)
  NX, // Neuro-X Master (IC, STI, SV)
  SIQ, // Quantum Science and Engineering (IC, SB, STI)
}

enum class AcademicLevel {
  BA1,
  BA2,
  BA3,
  BA4,
  BA5,
  BA6,
  MA1,
  MA2,
  MA3,
  MA4,
  PhD,
}

enum class Language {
  STUDENT,
  FRENCH,
  ENGLISH,
  GERMAN,
}

enum class TutoringSubject {
  ANALYSIS,
  ALGEBRA,
  PHYSICS,
}
