package com.github.se.project.ui.navigation

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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

  val isConnected by networkStatusViewModel.isConnected.collectAsState()
  val context = LocalContext.current

  Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.95f),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
          Box(modifier = Modifier.fillMaxWidth().height(16.dp))

          Row(
              modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceAround,
              verticalAlignment = Alignment.CenterVertically) {
                tabList.forEachIndexed { index, tab ->
                  val selected = tab.route == selectedItem
                  val enabled =
                      isConnected || tab.textId == home_text || tab.textId == my_course_text
                  val isMiddleItem = index == tabList.size / 2

                  if (isMiddleItem) {
                    Spacer(modifier = Modifier.width(72.dp))
                  } else {
                    Column(
                        modifier =
                            Modifier.weight(1f)
                                .animateContentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(
                                    enabled = enabled,
                                    onClick = {
                                      if (enabled) {
                                        onTabSelect(tab)
                                      } else {
                                        Toast.makeText(
                                                context,
                                                context.getString(R.string.inform_user_offline),
                                                Toast.LENGTH_SHORT)
                                            .show()
                                      }
                                    })
                                .padding(vertical = 4.dp)
                                .testTag(tab.textId)
                                .semantics { this.selected = selected },
                        horizontalAlignment = Alignment.CenterHorizontally) {
                          Icon(
                              imageVector = tab.icon,
                              contentDescription = null,
                              modifier = Modifier.size(24.dp).scale(if (selected) 1.2f else 1f),
                              tint =
                                  if (selected) {
                                    MaterialTheme.colorScheme.primary
                                  } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                  })

                          Text(
                              text = tab.textId,
                              style = MaterialTheme.typography.labelSmall,
                              color =
                                  if (selected) {
                                    MaterialTheme.colorScheme.primary
                                  } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                  },
                              modifier = Modifier.alpha(if (selected) 1f else 0.7f))

                          if (selected) {
                            Box(
                                modifier =
                                    Modifier.padding(top = 4.dp)
                                        .size(4.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape))
                          }
                        }
                  }
                }
              }
        }

    Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = (-28).dp)) {
      val centerItem = tabList[tabList.size / 2]
      val enabled =
          isConnected || centerItem.textId == home_text || centerItem.textId == my_course_text

      // Shadow ring
      Box(modifier = Modifier.size(64.dp).background(Color.Black.copy(alpha = 0.1f), CircleShape))

      FloatingActionButton(
          onClick = {
            if (enabled) {
              onTabSelect(centerItem)
            } else {
              Toast.makeText(
                      context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT)
                  .show()
            }
          },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          shape = CircleShape,
          modifier = Modifier.size(56.dp).align(Alignment.Center).testTag("centerElement")) {
            Icon(
                imageVector = centerItem.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp).scale(1.2f))
          }
    }
  }
}
