package com.github.se.project.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.network.NetworkStatusViewModel

/**
 * A composable function that displays a bottom navigation menu with a list of top-level
 * destinations.
 *
 * This menu allows users to navigate between different sections of the app.
 *
 * @param onTabSelect A lambda function that is invoked when a tab is selected. It receives the
 *   selected [TopLevelDestination] as a parameter.
 * @param tabList A list of [TopLevelDestination] items to be displayed in the navigation menu. Each
 *   destination should have a route, an icon, and a label.
 * @param selectedItem A string representing the route of the currently selected tab.
 *     * This will be used to highlight the active tab in the navigation menu.
 * @param networkStatusViewModel The [NetworkStatusViewModel] used to check the network status.
 *    * This will be used to show a toast message when the user is offline.
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String,
    networkStatusViewModel: NetworkStatusViewModel = viewModel()
) {

    val isConnected by networkStatusViewModel.isConnected.collectAsState()

    val context = LocalContext.current

  NavigationBar(modifier = Modifier.fillMaxWidth().height(60.dp).testTag("bottomNavigationMenu")) {
    tabList.forEach { tab ->
      NavigationBarItem(
          icon = { Icon(tab.icon, contentDescription = tab.textId) },
          label = { Text(tab.textId) },
          selected = tab.route == selectedItem,
          onClick = {
              if (isConnected) {
                  onTabSelect(tab) // Proceed with navigation if online
              } else {
                  // Show Toast if offline
                  Toast.makeText(context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT).show()
              }
          },
          modifier = Modifier.clip(RoundedCornerShape(50.dp)).testTag(tab.textId))
    }
  }
}
