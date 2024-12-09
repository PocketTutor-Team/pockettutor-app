package com.github.se.project.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
 *         * This will be used to highlight the active tab in the navigation menu.
 *
 * @param networkStatusViewModel The [NetworkStatusViewModel] used to check the network status.
 *     * This will be used to show a toast message when the user is offline.
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String,
    networkStatusViewModel: NetworkStatusViewModel
) {
  val home_text = stringResource(id = R.string.home)
  val my_course_text = stringResource(id = R.string.my_course)
  val find_tutor_text = stringResource(id = R.string.find_tutor)
  val find_student_text = stringResource(id = R.string.find_student)
  val chat_text = stringResource(id = R.string.chat)

  val isConnected by networkStatusViewModel.isConnected.collectAsState()

  val context = LocalContext.current

  NavigationBar(modifier = Modifier.fillMaxWidth().height(60.dp).testTag("bottomNavigationMenu")) {
    tabList.forEach { tab ->
      NavigationBarItem(
          icon = { Icon(tab.icon, contentDescription = tab.textId) },
          label = { Text(tab.textId) },
          selected = tab.route == selectedItem,
          colors =
              NavigationBarItemDefaults.colors(
                  indicatorColor = Color.Transparent,
              ),
          onClick = {
            // Proceed with navigation to any buttons if online or to home screen
            if (isConnected || tab.textId == home_text || tab.textId == my_course_text) {
              onTabSelect(tab)
              // only block and show the toast when user try to navigate to find tutor or find
              // student or chat screen
            } else if (tab.textId == find_tutor_text ||
                tab.textId == find_student_text ||
                tab.textId == chat_text) {
              // Show Toast if offline
              Toast.makeText(
                      context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT)
                  .show()
            }
          },
          modifier = Modifier.clip(RoundedCornerShape(50.dp)).testTag(tab.textId))
    }
  }
}
