package com.github.se.project.model.profile

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
    val languages: List<Language> = listOf(),
    val subjects: List<Subject> = listOf(),
    val schedule: List<List<Int>> = List(7) { List(12) { 0 } }, // Weekly schedule
    var price: Int = 0,
)
