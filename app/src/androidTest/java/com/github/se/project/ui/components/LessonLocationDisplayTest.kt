package com.github.se.project.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.ui.lesson.TutorLessonResponseScreen
import org.junit.Rule
import org.junit.Test

class LessonLocationDisplayTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)


    @Test
    fun everyComponentsAreDisplayed_LessonLocation() {
        var isLocationChecked = false
        composeTestRule.setContent {
            LessonLocationDisplay(
                latitude = 37.7749,
                longitude = -122.4194,
                lessonTitle = "Physics Tutoring",
                onLocationChecked = { isLocationChecked = true }
            )
        }
        composeTestRule.waitUntil(15000) {
            // wait max 15 seconds for the map to load,
            // as soon as the map is ready, the next line will be executed
            isLocationChecked
        }
        isLocationChecked = false

        composeTestRule.onNodeWithTag("lessonLocationColumn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lessonLocationTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lessonLocationTitle").assertTextEquals("Lesson Location")
        composeTestRule.onNodeWithTag("lessonLocationCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lessonLocationMap").assertIsDisplayed()

    }
}

