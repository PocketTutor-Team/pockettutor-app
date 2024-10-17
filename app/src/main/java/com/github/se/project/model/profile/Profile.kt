package com.github.se.project.model.profile

import java.util.EnumSet

/** Data class representing a user profile. */
data class Profile(
    val uid: String, // Unique string id for the profile
    val googleUid: String, // Google unique user id
    val firstName: String, // First name of the user
    val lastName: String, // Last name of the user
    val phoneNumber: String, // Phone number of the user
    val role: Role, // Role of the user
    val section: Section, // Section of the user
    val academicLevel: AcademicLevel, // Academic level of the user
    // TODO: update languages and subjects to avoid using EnumSet
    val languages: EnumSet<Language> = EnumSet.noneOf(Language::class.java),
    val subjects: EnumSet<Subject> = EnumSet.noneOf(Subject::class.java),
    val schedule: List<List<Int>> = List(7) { List(12) { 0 } }, // Weekly schedule
    var price: Int = 0,
)
