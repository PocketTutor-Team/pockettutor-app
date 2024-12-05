package com.github.se.project.utils

data class Country(val name: String, val code: String, val flagEmoji: String)

val countries =
    listOf(
        Country("Switzerland", "+41", "\uD83C\uDDE8\uD83C\uDDED"),
        Country("France", "+33", "\uD83C\uDDEB\uD83C\uDDF7"),
        Country("Germany", "+49", "\uD83C\uDDE9\uD83C\uDDEA"),
        Country("United Kingdom", "+44", "\uD83C\uDDEC\uD83C\uDDE7"),
        Country("United States", "+1 ", "\uD83C\uDDFA\uD83C\uDDF8"),
        Country("Canada", "+1 ", "\uD83C\uDDE8\uD83C\uDDE6"),
        Country("Australia", "+61", "\uD83C\uDDE6\uD83C\uDDFA"),
        Country("Japan", "+81", "\uD83C\uDDEF\uD83C\uDDF5"),
        Country("China", "+86", "\uD83C\uDDE8\uD83C\uDDF3"),
        Country("India", "+91", "\uD83C\uDDEE\uD83C\uDDF3"),
        Country("Brazil", "+55", "\uD83C\uDDE7\uD83C\uDDF7"),
        Country("Mexico", "+52", "\uD83C\uDDF2\uD83C\uDDFD"),
        Country("Italy", "+39", "\uD83C\uDDEE\uD83C\uDDF9"),
        Country("Spain", "+34", "\uD83C\uDDEA\uD83C\uDDF8"),
        Country("Netherlands", "+31", "\uD83C\uDDF3\uD83C\uDDF1"))
