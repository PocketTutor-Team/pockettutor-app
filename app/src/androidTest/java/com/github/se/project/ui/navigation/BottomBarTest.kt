package com.github.se.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.network.NetworkStatusViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomBarTest {

  @get:Rule val composeTestRule = createComposeRule()
  // Mock NetworkStatusViewModel to control the network status state
  private val mockIsConnected = MutableStateFlow(true)
  private lateinit var networkStatusViewModel: NetworkStatusViewModel

  @Before
  fun setup() {
    networkStatusViewModel =
        object :
            NetworkStatusViewModel(
                application = androidx.test.core.app.ApplicationProvider.getApplicationContext()) {
          override val isConnected = mockIsConnected
        }
  }

  @Test
  fun bottomNavigationMenu_displaysTabs() {
    val tabList =
        listOf(
            TopLevelDestination("home", Icons.Default.Home, "Home"),
            TopLevelDestination("search", Icons.Default.Search, "Search"),
            TopLevelDestination("profile", Icons.Default.Person, "Profile"))

    composeTestRule.setContent {
      BottomNavigationMenu(
          onTabSelect = {}, tabList = tabList, selectedItem = "home", networkStatusViewModel)
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
      BottomNavigationMenu(
          onTabSelect = {}, tabList = tabList, selectedItem = "search", networkStatusViewModel)
    }

    composeTestRule.onNodeWithTag("Search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Search").assertIsSelected()
  }
}
