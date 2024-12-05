package com.github.se.project.model.network

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel that provides the network status to the UI.
 *
 * @param application The application context.
 */
open class NetworkStatusViewModel(application: Application) : AndroidViewModel(application) {

  private val connectivityManager = application.getSystemService(ConnectivityManager::class.java)

  private val _isConnected = MutableStateFlow(true) // Initial value
  open val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

  init {
    val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
          override fun onAvailable(network: Network) {
            _isConnected.value = true
          }

          override fun onLost(network: Network) {
            _isConnected.value = false
          }
        }
    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }
}
