package com.github.se.project.utils

/**
 * Capitalizes the first letter of the string and converts the rest of the characters to lowercase.
 *
 * Example:
 * ```
 * "john".capitalizeFirstLetter() // Output: "John"
 * "aLiCe".capitalizeFirstLetter() // Output: "Alice"
 * ```
 *
 * @return A new string with the first letter capitalized and the remaining characters in lowercase.
 */
fun String.capitalizeFirstLetter(): String {
  return this.lowercase().replaceFirstChar { it.uppercase() }
}
