package com.github.se.project.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.project.model.profile.Profile
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A ViewModel responsible for managing chat-related state and interactions with the Stream Chat
 * client.
 *
 * @param chatClient The ChatClient instance used for interacting with the chat backend.
 */
open class ChatViewModel(val chatClient: ChatClient) : ViewModel() {
  // Mutable state for the current channel ID, used to track which chat channel is active
  private val currentChannelID_ = MutableStateFlow<String?>(null)
  val currentChannelID = currentChannelID_.asStateFlow() // Exposes an immutable flow for observers

  // Tracks whether the user is already connected to avoid duplicate connections
  private var connected = false

  // Represents the initialization state of the ChatClient (e.g., not initialized, initializing, or
  // complete)
  val clientInitialisationState = chatClient.clientState.initializationState

  companion object {
    /**
     * A factory method for creating an instance of ChatViewModel with the provided ChatClient.
     *
     * @param chatClient The ChatClient instance used by the ViewModel.
     */
    fun Factory(chatClient: ChatClient): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatClient) as T
          }
        }
  }

  /**
   * Connects the provided user profile to the Stream Chat client.
   *
   * @param profile The user's profile containing their unique ID, first name, and last name.
   */
  open fun connect(profile: Profile) {
    if (connected) return // Prevents duplicate connections
    connected = true
    val user =
        User(
            id = profile.uid, // Unique user ID
            name = "${profile.firstName} ${profile.lastName}" // User's full name
            )
    val token = chatClient.devToken(user.id) // Generates a development token for the user
    chatClient.connectUser(user, token).enqueue() // Connects the user to the chat backend
  }

  /**
   * Ensures a channel exists for the given list of member UIDs.
   *
   * @param channelMembersUid The list of user IDs that will be members of the channel.
   */
  open fun ensureChannelExists(channelMembersUid: List<String>) {
    chatClient
        .createChannel(
            channelType = "messaging", // Specifies the type of the channel
            channelId = "", // Automatically generates a unique channel ID
            memberIds = channelMembersUid, // Adds the specified users as members of the channel
            extraData = emptyMap() // Allows for additional metadata (none in this case)
            )
        .enqueue { result ->
          if (result.isSuccess) {
            // Channel creation was successful
          } else {
            // Logs the error message if channel creation fails
            Log.e("ChatViewModel", "Error creating channel: " + result.errorOrNull()?.message)
          }
        }
  }

  /**
   * Updates the currently active channel ID.
   *
   * @param channelID The ID of the channel to set as active. Pass `null` to clear the current
   *   channel.
   */
  open fun setChannelID(channelID: String?) {
    currentChannelID_.value = channelID
  }
}
