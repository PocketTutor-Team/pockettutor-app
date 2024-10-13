package com.github.se.project.model.profile

import java.util.EnumSet

/** Data class representing a user profile. */
data class Profile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val role: Role,
    val section: Section,
    val academicLevel: AcademicLevel,
    val languages: EnumSet<Language> = EnumSet.noneOf(Language::class.java),
    val subjects: EnumSet<TutoringSubject> = EnumSet.noneOf(TutoringSubject::class.java),
    val schedule: List<List<Int>> = List(7) { List(12) { 0 } },
    var price: Int = 0
    /*TODO: Add profile picture*/
)

data class ProfileUpload(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val role: String,
    val section: String,
    val academicLevel: String,
    val languages: MutableList<String> = mutableListOf(),
    val subjects: MutableList<String> = mutableListOf(),
    val scheduleFlat: List<Int> = List(7 * 12) { 0 }, // Flattened version of weekly schedule
    var price: Int = 0
)

fun profileUpload(profile: Profile): ProfileUpload {
  return ProfileUpload(
      uid = profile.uid,
      firstName = profile.firstName,
      lastName = profile.lastName,
      phoneNumber = profile.phoneNumber,
      role = profile.role.name,
      section = profile.section.name,
      academicLevel = profile.academicLevel.name,
      languages = profile.languages.map { it.name }.toMutableList(),
      subjects = profile.subjects.map { it.name }.toMutableList(),
      scheduleFlat = profile.schedule.flatten(),
      price = profile.price)
}

fun profileFromUpload(profileUpload: ProfileUpload): Profile {
  return Profile(
      uid = profileUpload.uid,
      firstName = profileUpload.firstName,
      lastName = profileUpload.lastName,
      phoneNumber = profileUpload.phoneNumber,
      role = Role.valueOf(profileUpload.role),
      section = Section.valueOf(profileUpload.section),
      academicLevel = AcademicLevel.valueOf(profileUpload.academicLevel),
      languages =
          profileUpload.languages
              .map { Language.valueOf(it) }
              .toCollection(EnumSet.noneOf(Language::class.java)),
      subjects =
          profileUpload.subjects
              .map { TutoringSubject.valueOf(it) }
              .toCollection(EnumSet.noneOf(TutoringSubject::class.java)),
      schedule = profileUpload.scheduleFlat.chunked(12),
      price = profileUpload.price)
}

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
  FRENCH,
  ENGLISH,
  GERMAN,
}

enum class TutoringSubject {
  ANALYSIS,
  ALGEBRA,
  PHYSICS,
}
