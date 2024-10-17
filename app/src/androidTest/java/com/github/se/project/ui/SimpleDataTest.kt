package com.github.se.project.ui

import com.github.se.project.Point
import org.junit.Test

class SimpleDataTest {

  @Test
  fun testPointDistance() {
    val p1 = Point(0.0, 0.0)
    val p2 = Point(3.0, 4.0)
    assert(p1.distanceTo(p2) == 5.0)
  }
}
