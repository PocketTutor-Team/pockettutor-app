package com.github.se.project.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {

  private val _currentChannelId = MutableStateFlow<String?>(null)
  val currentChannelId = _currentChannelId.asStateFlow()

  private var isConnected = false

  val clientInitializationState = chatClient.clientState.initializationState

  companion object {
    fun Factory(chatClient: ChatClient): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatClient) as T
          }
        }
  }

  /**
   * Connects the user to the Stream Chat client.
   *
   * @param profile The user's profile containing uid, firstName, and lastName.
   */
  fun connectUser(profile: Profile) {
    if (isConnected) return // Prevent duplicate connections

    val user =
        User(
            id = profile.uid,
            name = "${profile.firstName} ${profile.lastName}",
        )

    val token = chatClient.devToken(user.id)

    chatClient.connectUser(user, token).enqueue { result ->
      if (result.isSuccess) {
        isConnected = true
        Log.d("ChatViewModel", "User connected successfully.")
      } else {
        Log.e("ChatViewModel", "Error connecting user: ${result.errorOrNull()}")
      }
    }
  }

  /**
   * Creates or retrieves a channel based on lesson participants.
   *
   * @param lesson The confirmed lesson object.
   */
  fun createOrGetChannel(
      lesson: Lesson,
      onChannelCreated: (io.getstream.chat.android.models.Channel) -> Unit = {},
      otherUsername: String
  ) {
    val tutorId = lesson.tutorUid
    val studentId = lesson.studentUid

    // Generate a consistent channel ID
    val channelId = generateChannelId(tutorId[0], studentId)

    val channelMembers = listOf(tutorId[0], studentId)

    var extraData = mapOf("name" to "$otherUsername")

    chatClient
        .createChannel(
            channelType = "messaging",
            channelId = channelId,
            memberIds = channelMembers,
            extraData = extraData)
        .enqueue { result ->
          if (result.isSuccess) {
            val createdChannel = result.getOrNull()
            Log.d("ChatViewModel", "Channel $channelId created or retrieved successfully.")
            if (createdChannel != null) {
              onChannelCreated(createdChannel)
            }
          } else {
            Log.e("ChatViewModel", "Error creating channel: ${result.errorOrNull()}")
          }
        }
  }

  /** Generates a consistent channel ID based on user IDs. */
  private fun generateChannelId(userId1: String, userId2: String): String {
    // Ensure the channel ID is the same regardless of the order of user IDs
    return listOf(userId1, userId2).sorted().joinToString("_")
  }

  /** Sets the current active channel ID. */
  fun setCurrentChannelId(channelId: String?) {
    _currentChannelId.value = channelId
  }
}
