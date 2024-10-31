package com.github.se.project.ui.navigation

import androidx.navigation.NavHostController
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NavigationActionsTest {

  private lateinit var navController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navController = mock()
    navigationActions = NavigationActions(navController)
  }

  @Test
  fun testGoBack() {
    navigationActions.goBack()
    verify(navController).popBackStack()
  }

  @Test
  fun testCurrentRouteWithoutDestination() {
    whenever(navController.currentDestination).thenReturn(null)
    assert(navigationActions.currentRoute().isEmpty())
  }
}
