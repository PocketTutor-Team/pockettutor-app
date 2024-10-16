package com.github.se.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.TutoringSubject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.test.CreateTutorProfile
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.EnumSet

@RunWith(AndroidJUnit4::class)
class BottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomNavigationMenu_displaysTabs() {
        val tabList = listOf(
            TopLevelDestination("home", Icons.Default.Home, "Home"),
            TopLevelDestination("search", Icons.Default.Search, "Search"),
            TopLevelDestination("profile", Icons.Default.Person, "Profile")
        )

        composeTestRule.setContent {
            BottomNavigationMenu(
                onTabSelect = {},
                tabList = tabList,
                selectedItem = "home"
            )
        }

        tabList.forEach { tab ->
            composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed()
        }
    }

    @Test
    fun bottomNavigationMenu_highlightsSelectedTab() {
        val tabList = listOf(
            TopLevelDestination("home", Icons.Default.Home, "Home"),
            TopLevelDestination("search", Icons.Default.Search, "Search"),
            TopLevelDestination("profile", Icons.Default.Person, "Profile")
        )

        composeTestRule.setContent {
            BottomNavigationMenu(
                onTabSelect = {},
                tabList = tabList,
                selectedItem = "search"
            )
        }

        composeTestRule.onNodeWithTag("Search").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Search").assertIsSelected()
    }
}