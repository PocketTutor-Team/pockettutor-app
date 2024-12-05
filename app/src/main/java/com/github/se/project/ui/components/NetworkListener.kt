package com.github.se.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.network.NetworkStatusViewModel

/**
 * A composable that listens to the network status and displays a red banner to inform the user at
 * the top of the screen when the device is offline.
 *
 * @param networkStatusViewModel The ViewModel that provides the network status.
 * @param content The content to display when the device is online.
 */
@Composable
fun NetworkStatusListener(
    networkStatusViewModel: NetworkStatusViewModel = viewModel(),
    content: @Composable () -> Unit
) {

  val isConnected by networkStatusViewModel.isConnected.collectAsState()

  // Display the UI
  Column {
    // Show the offline banner only when offline
    if (!isConnected) {
      Box(
          modifier = Modifier.fillMaxWidth().height(30.dp).background(Color.Red),
          contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = stringResource(id = R.string.no_internet_connection),
                  color = Color.White,
                  fontSize = 14.sp,
                  modifier = Modifier.testTag("offline_text"))

              Spacer(modifier = Modifier.width(8.dp))

              Icon(
                  painter = painterResource(id = R.drawable.baseline_wifi_off_24),
                  contentDescription = "Offline",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp).testTag("offline_icon"))
            }
          }
    }

    // Display the rest of the app content below the banner
    content()
  }
}
