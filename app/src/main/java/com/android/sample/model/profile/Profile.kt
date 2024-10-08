package com.android.sample.model.profile


data class Profile (
    val uid: String, // Unique string id for the profile
    val firstName: String, // First name of the profile
    val lastName: String, // Last name of the profile
    val role: Role, // Role of the profile
    val section: Section, // Section of the profile
    val academicLevel: AcademicLevel, // Academic level of the profile

    val schedule : List<List<Int>>, // Weekly schedule of the profile (7 days x 12 slots)

    val email: String, // Email of the profile
)

enum class Role {
    STUDENT, TUTOR, UNKNOWN
}

enum class Section {
    IN, SC, // School of Computer and Communication Sciences (IC)
    AR, GC, SIE, // School of Architecture, Civil and Environmental Engineering (ENAC)
    SGC, MA, PH, // School of Basic Sciences (SB)
    EL, MX, GM, MT, // School of Engineering (STI)
    SV, // School of Life Sciences (SV)
    NX, // Neuro-X Master (IC, STI, SV)
    SIQ, // Quantum Science and Engineering (IC, SB, STI)
    UNKNOWN
}

enum class AcademicLevel {
    BA1, BA2, BA3, BA4, BA5, BA6,
    MA1, MA2, MA3, MA4,
    PhD,
    UNKNOWN
}