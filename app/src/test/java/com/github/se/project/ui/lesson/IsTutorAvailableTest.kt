package com.github.se.project.ui.lesson

import org.junit.Test

class IsTutorAvailableTest {
  @Test
  fun isTutorAvailable_respondToEdgeCases() {
    // check if exception thrown when tutor schedule is empty
    try {
      isTutorAvailable(listOf(), "01/01/2050T10:00:00")
      assert(false)
    } catch (e: Exception) {
      assert(e is IllegalArgumentException)
    }

    try {
      isTutorAvailable(List(7) { listOf() }, "01/01/2050T10:00:00")
      assert(false)
    } catch (e: Exception) {
      assert(e is IllegalArgumentException)
    }

    // check if exception thrown when timeslot is invalid
    try {
      isTutorAvailable(List(7) { List(12) { 1 } }, "invalid")
      assert(false)
    } catch (e: Exception) {
      assert(e is IllegalArgumentException)
    }

    // check if false is return when hourIndex is not in 0..11
    assert(!isTutorAvailable(List(7) { List(12) { 1 } }, "01/01/2050T06:00:00"))
    assert(!isTutorAvailable(List(7) { List(12) { 1 } }, "01/01/2050T50:00:00"))

    // check if false is return when schedule not available
    assert(!isTutorAvailable(List(7) { List(12) { 0 } }, "01/01/2050T10:00:00"))
  }

  @Test
  fun isTutorAvailable_returnFalseWhenNotAvailable() {
    // check if false is return when schedule not available
    for (i in 8..19) {
      val paddedI = i.toString().padStart(2, '0')
      assert(!isTutorAvailable(List(7) { List(12) { 0 } }, "01/01/2050T${paddedI}:00:00"))
    }
  }
}
