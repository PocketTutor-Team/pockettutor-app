package com.github.se.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

// Define routes
object Route {
  const val WELCOME = "Welcome"
  const val HOME = "Home"
  const val AUTH = "Auth"
  const val SEARCH = "Search"
}

// Define screens
object Screen {
  const val WELCOME = "Welcome Screen"
  const val HOME = "Home Screen" // Overview screen
  const val AUTH = "Auth Screen"
  const val PROFILE = "Profile Screen"
  const val CREATE_PROFILE = "Create profile Screen"
  const val EDIT_PROFILE = "Edit profile Screen"
  const val CALENDAR = "Calendar Screen"
  const val ADD_LESSON = "Add Lesson"
  const val SEARCH = "Search Screen" // Find tutor / find student screen
}

// Data class for top-level destinations
data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

// Top-level destinations with icons
object TopLevelDestinations {
  val HOME = TopLevelDestination(route = Route.HOME, icon = Icons.Outlined.Home, textId = "Home")
  val SEARCH =
      TopLevelDestination(route = Route.SEARCH, icon = Icons.Outlined.Search, textId = "Search")
}

// List of top-level destinations
val LIST_TOP_LEVEL_DESTINATIONS = listOf(TopLevelDestinations.HOME, TopLevelDestinations.SEARCH)

open class NavigationActions(
    private val navController: NavHostController,
) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      if (destination.route != Route.AUTH) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
}
