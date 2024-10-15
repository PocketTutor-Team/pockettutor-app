package com.github.se.project.ui.profile

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

public class CreateUserProfileUnitTest {
  @Test
  fun testValidPhoneNumberWithPlus() {
    val validPhoneNumber = "+41441234567"
    assertTrue(isPhoneNumberValid(validPhoneNumber))
  }

  @Test
  fun testValidPhoneNumberWithoutPlus() {
    val validPhoneNumber = "41441234567"
    assertTrue(isPhoneNumberValid(validPhoneNumber))
  }

  @Test
  fun testValidPhoneNumberWithMaxDigits() {
    val validPhoneNumber = "+41441234567890"
    assertTrue(isPhoneNumberValid(validPhoneNumber))
  }

  @Test
  fun testInvalidPhoneNumberTooShort() {
    val invalidPhoneNumber = "+414"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber))
  }

  @Test
  fun testInvalidPhoneNumberWithLetters() {
    val invalidPhoneNumber = "+41ABC123456"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber))
  }

  @Test
  fun testInvalidPhoneNumberWithSpecialChars() {
    val invalidPhoneNumber = "+4144-123-456"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber))
  }

  @Test
  fun testInvalidPhoneNumberTooLong() {
    val invalidPhoneNumber = "+414412345678901234"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber))
  }
}
