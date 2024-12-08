package com.github.se.project.model.profile

import com.github.se.project.model.certification.EpflCertification

/** Data class representing a user profile. */
data class Profile(
    val uid: String, // Unique string id for the profile
    var token: String = "", // Token for notifications
    val googleUid: String, // Google unique user id
    var firstName: String, // First name of the user
    var lastName: String, // Last name of the user
    var phoneNumber: String, // Phone number of the user
    val role: Role, // Role of the user
    var section: Section, // Section of the user
    var academicLevel: AcademicLevel, // Academic level of the user
    var description: String = "", // Description of the user
    var languages: List<Language> = listOf(),
    var subjects: List<Subject> = listOf(),
    var schedule: List<List<Int>> = List(7) { List(12) { 0 } }, // Weekly schedule
    var price: Int = 0,
    var certification: EpflCertification? = null
)
