package com.github.se.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigationMenu_displaysTabs() {
    val tabList =
        listOf(
            TopLevelDestination("home", Icons.Default.Home, "Home"),
            TopLevelDestination("search", Icons.Default.Search, "Search"),
            TopLevelDestination("profile", Icons.Default.Person, "Profile"))

    composeTestRule.setContent {
      BottomNavigationMenu(onTabSelect = {}, tabList = tabList, selectedItem = "home")
    }

    tabList.forEach { tab -> composeTestRule.onNodeWithTag(tab.textId).assertIsDisplayed() }
  }

  @Test
  fun bottomNavigationMenu_highlightsSelectedTab() {
    val tabList =
        listOf(
            TopLevelDestination("home", Icons.Default.Home, "Home"),
            TopLevelDestination("search", Icons.Default.Search, "Search"),
            TopLevelDestination("profile", Icons.Default.Person, "Profile"))

    composeTestRule.setContent {
      BottomNavigationMenu(onTabSelect = {}, tabList = tabList, selectedItem = "search")
    }

    composeTestRule.onNodeWithTag("Search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Search").assertIsSelected()
  }
}
