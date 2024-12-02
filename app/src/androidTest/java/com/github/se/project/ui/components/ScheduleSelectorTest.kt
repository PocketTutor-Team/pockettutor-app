package com.github.se.project.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.ui.theme.SampleAppTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun availabilityGridDisplaysCorrectly() {
        val schedule = List(7) { List(24) { 0 } }
        composeTestRule.setContent {
            SampleAppTheme {
                AvailabilityGrid(
                    schedule = schedule,
                    onScheduleChange = {},
                    modifier = Modifier.testTag("availabilityGrid")
                )
            }
        }

        // Check that the grid is displayed
        composeTestRule.onNodeWithTag("availabilityGrid").assertIsDisplayed()

        // Check that the "Select All" and "Unselect All" buttons are displayed
        composeTestRule.onNodeWithText("Select All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Unselect All").assertIsDisplayed()
    }

    @Test
    fun selectingIndividualSlotUpdatesSchedule() {
        val scheduleState = mutableStateOf(List(7) { List(24) { 0 } })
        composeTestRule.setContent {
            SampleAppTheme {
                AvailabilityGrid(
                    schedule = scheduleState.value,
                    onScheduleChange = { updatedSchedule -> scheduleState.value = updatedSchedule },
                    modifier = Modifier.testTag("availabilityGrid")
                )
            }
        }

        // Define the slot to test
        val dayIndex = 1 // Tuesday (assuming 0 = Monday)
        val hourIndex = 2 // 10 h (since hours start from 8 h)

        // Build the test tag for the slot
        val slotTag = "Slot_${dayIndex}_${hourIndex}"

        // Verify the slot is initially unselected
        assertEquals(
            "Initial slot value should be 0 (unselected)",
            0,
            scheduleState.value[dayIndex][hourIndex]
        )

        // Click the slot to select it
        composeTestRule.onNodeWithTag(slotTag).performClick()

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Verify the slot is now selected
        assertEquals(
            "Slot value should be 1 (selected) after click",
            1,
            scheduleState.value[dayIndex][hourIndex]
        )

        // Click the slot again to deselect it
        composeTestRule.onNodeWithTag(slotTag).performClick()

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Verify the slot is now unselected
        assertEquals(
            "Slot value should be 0 (unselected) after second click",
            0,
            scheduleState.value[dayIndex][hourIndex]
        )
    }

    @Test
    fun selectAllButtonUpdatesSchedule() {
        val scheduleState = mutableStateOf(List(7) { List(24) { 0 } })
        composeTestRule.setContent {
            SampleAppTheme {
                AvailabilityGrid(
                    schedule = scheduleState.value,
                    onScheduleChange = { updatedSchedule -> scheduleState.value = updatedSchedule },
                    modifier = Modifier.testTag("availabilityGrid")
                )
            }
        }

        // Click on the "Select All" button
        composeTestRule.onNodeWithText("Select All").performClick()

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Verify all slots are selected
        val allSelected = scheduleState.value.all { dayList -> dayList.all { it == 1 } }
        assertTrue("All slots should be selected after clicking 'Select All'", allSelected)
    }

    @Test
    fun unselectAllButtonUpdatesSchedule() {
        val scheduleState = mutableStateOf(List(7) { List(24) { 1 } }) // All slots selected initially
        composeTestRule.setContent {
            SampleAppTheme {
                AvailabilityGrid(
                    schedule = scheduleState.value,
                    onScheduleChange = { updatedSchedule -> scheduleState.value = updatedSchedule },
                    modifier = Modifier.testTag("availabilityGrid")
                )
            }
        }

        // Click on the "Unselect All" button
        composeTestRule.onNodeWithText("Unselect All").performClick()

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Verify all slots are unselected
        val allUnselected = scheduleState.value.all { dayList -> dayList.all { it == 0 } }
        assertTrue("All slots should be unselected after clicking 'Unselect All'", allUnselected)
    }
}
