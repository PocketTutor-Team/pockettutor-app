package com.github.se.project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

// Define routes
object Route {
  const val HOME = "Home"
  const val AUTH = "Auth"
  const val FIND_TUTOR = "Find a Tutor"
  const val FIND_STUDENT = "Find a Lesson"
}

// Define screens
object Screen {
  const val SPLASH = "Splash Screen"
  const val HOME = "Home Screen" // Overview screen
  const val AUTH = "Auth Screen"
  const val PROFILE = "Profile Screen"
  const val CREATE_PROFILE = "Create profile Screen"
  const val EDIT_PROFILE = "Edit profile Screen"
  const val EDIT_SCHEDULE = "Edit schedule Screen"
  const val CREATE_TUTOR_PROFILE = "Tutor information creation Screen"
  const val FAVORITE_TUTORS = "Favorite Tutors Screen"
  const val FAVORITE_TUTOR_DETAILS = "Favorite Tutor Details Screen"
  const val ADD_LESSON_WITH_FAVORITE = "Add Lesson with a Favorite Tutor Screen"
  const val ADD_LESSON = "Add Lesson Screen"
  const val TUTOR_MATCH = "Tutor matching Screen"
  const val SELECTED_TUTOR_DETAILS = "Selected Tutor Details Screen"
  const val EDIT_REQUESTED_LESSON = "Edit Requested Lesson"
  const val CONFIRMED_LESSON = "Confirmed Lesson Screen"
  const val COMPLETED_LESSON = "Completed Lesson Screen"
  const val CREATE_TUTOR_SCHEDULE = "Create tutor calendar Screen"
  const val LESSONS_REQUESTED = "Lessons Requested Screen"
  const val TUTOR_LESSON_RESPONSE = "Tutor Lesson Response Screen"
  const val CHANNEL = "Channel Screen"
  const val CHAT = "Chat Screen"
}

// Data class for top-level destinations
data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

// Top-level destinations with icons
object TopLevelDestinations {
  val HOME_TUTOR =
      TopLevelDestination(route = Screen.HOME, icon = Icons.Outlined.Home, textId = "Home")
  val HOME_STUDENT =
      TopLevelDestination(route = Screen.HOME, icon = Icons.Outlined.Home, textId = "My Courses")
  val STUDENT =
      TopLevelDestination(
          route = Route.FIND_TUTOR, icon = Icons.Outlined.Add, textId = "Find Tutor")
  val TUTOR =
      TopLevelDestination(
          route = Route.FIND_STUDENT, icon = Icons.Outlined.Search, textId = "Find Student")
  val CHANNEL =
      TopLevelDestination(route = Screen.CHANNEL, icon = Icons.Outlined.Email, textId = "Chat")
}

// List of top-level destinations
val LIST_TOP_LEVEL_DESTINATIONS_TUTOR =
    listOf(
        TopLevelDestinations.HOME_TUTOR, TopLevelDestinations.TUTOR, TopLevelDestinations.CHANNEL)
val LIST_TOP_LEVEL_DESTINATIONS_STUDENT =
    listOf(
        TopLevelDestinations.HOME_STUDENT,
        TopLevelDestinations.STUDENT,
        TopLevelDestinations.CHANNEL)

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
