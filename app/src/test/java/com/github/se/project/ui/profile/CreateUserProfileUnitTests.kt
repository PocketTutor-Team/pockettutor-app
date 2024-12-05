package com.github.se.project.ui.profile

import com.github.se.project.ui.components.isPhoneNumberValid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

public class CreateUserProfileUnitTest {
  @Test
  fun testValidPhoneNumberWithPlus() {
    val validPhoneNumber = "+41441234567"
    assertTrue(isPhoneNumberValid(validPhoneNumber.take(3), validPhoneNumber.drop(3)))
  }

  @Test
  fun testValidPhoneNumberWithoutPlus() {
    val validPhoneNumber = "41441234567"
    assertTrue(isPhoneNumberValid(validPhoneNumber.take(3), validPhoneNumber.drop(3)))
  }

  @Test
  fun testValidPhoneNumberWithMaxDigits() {
    val validPhoneNumber = "+41441234567890"
    assertTrue(isPhoneNumberValid(validPhoneNumber.take(3), validPhoneNumber.drop(3)))
  }

  @Test
  fun testInvalidPhoneNumberTooShort() {
    val invalidPhoneNumber = "+414"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber.take(3), invalidPhoneNumber.drop(3)))
  }

  @Test
  fun testInvalidPhoneNumberWithLetters() {
    val invalidPhoneNumber = "+41ABC123456"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber.take(3), invalidPhoneNumber.drop(3)))
  }

  @Test
  fun testInvalidPhoneNumberWithSpecialChars() {
    val invalidPhoneNumber = "+4144-123-456"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber.take(3), invalidPhoneNumber.drop(3)))
  }

  @Test
  fun testInvalidPhoneNumberTooLong() {
    val invalidPhoneNumber = "+414412345678901234"
    assertFalse(isPhoneNumberValid(invalidPhoneNumber.take(3), invalidPhoneNumber.drop(3)))
  }
}
