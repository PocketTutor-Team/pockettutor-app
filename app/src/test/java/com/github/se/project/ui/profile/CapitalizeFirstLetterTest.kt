package com.github.se.project.ui.profile

import com.github.se.project.utils.capitalizeFirstLetter
import org.junit.Assert.assertEquals
import org.junit.Test

class CapitalizeFirstLetterTest {

  @Test
  fun capitalizeFirstLetter_regularName() {
    val input = "john"
    val result = input.capitalizeFirstLetter()
    assertEquals("John", result)
  }

  @Test
  fun capitalizeFirstLetter_mixedCaseName() {
    val input = "aLiCe"
    val result = input.capitalizeFirstLetter()
    assertEquals("Alice", result)
  }

  @Test
  fun capitalizeFirstLetter_emptyString() {
    val input = ""
    val result = input.capitalizeFirstLetter()
    assertEquals("", result)
  }

  @Test
  fun capitalizeFirstLetter_singleLowercaseCharacter() {
    val input = "j"
    val result = input.capitalizeFirstLetter()
    assertEquals("J", result)
  }

  @Test
  fun capitalizeFirstLetter_singleUppercaseCharacter() {
    val input = "J"
    val result = input.capitalizeFirstLetter()
    assertEquals("J", result)
  }

  @Test
  fun capitalizeFirstLetter_specialCharacters() {
    val input = "@john"
    val result = input.capitalizeFirstLetter()
    assertEquals("@john", result) // No letter to capitalize
  }

  @Test
  fun capitalizeFirstLetter_nameWithNumbers() {
    val input = "123john"
    val result = input.capitalizeFirstLetter()
    assertEquals("123john", result) // No letter to capitalize
  }

  @Test
  fun capitalizeFirstLetter_alreadyCapitalized() {
    val input = "John"
    val result = input.capitalizeFirstLetter()
    assertEquals("John", result)
  }

  @Test
  fun capitalizeFirstLetter_onlyWhitespace() {
    val input = "   "
    val result = input.capitalizeFirstLetter()
    assertEquals("   ", result) // No letter to capitalize
  }

  @Test
  fun capitalizeFirstLetter_whitespaceBeforeName() {
    val input = "  john"
    val result = input.capitalizeFirstLetter()
    assertEquals("  john", result) // Does not trim the whitespace
  }
}
