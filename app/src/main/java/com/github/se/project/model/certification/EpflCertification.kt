package com.github.se.project.model.certification

data class EpflCertification(
    val sciper: String,
    val verified: Boolean = false,
    val verificationDate: Long = System.currentTimeMillis()
)
