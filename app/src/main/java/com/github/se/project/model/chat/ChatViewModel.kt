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

/**
 * ViewModel responsible for managing chat-related functionality, such as user connection and
 * channel management.
 *
 * This ViewModel interacts with the Stream Chat SDK to manage the connection of users, and channel
 * creation/retrieval for specific lessons between tutors and students.
 */
open class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {

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
    if (isConnected || chatClient == null)
        return // Prevent duplicate connections, chatClient can be null for tests

    val user =
        User(
            id = profile.uid,
            name = "${profile.firstName} ${profile.lastName}",
        )

    val token = chatClient.devToken(user.id)

    chatClient.connectUser(user, token).enqueue { result ->
      if (result.isSuccess) {
        isConnected = true
      } else {
        Log.e("ChatViewModel", "Error connecting user: ${result.errorOrNull()}")
      }
    }
  }

  /**
   * Creates or retrieves a chat channel for a lesson between the tutor and student.
   *
   * The channel ID is generated based on the tutor and student UID to ensure the same channel is
   * used for each lesson. The function handles both creating a new channel or retrieving an
   * existing one.
   *
   * @param lesson The object containing the tutor and student UIDs for the lesson.
   * @param onChannelCreated A callback that is invoked once the channel is successfully created or
   *   retrieved.
   * @param otherUsername The username of the other participant (either tutor or student) in the
   *   lesson, used for extra data in the channel.
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

  /**
   * Generates a consistent and unique channel ID based on two user IDs (either tutor or student).
   *
   * This method sorts the user IDs to ensure the channel ID is always the same regardless of the
   * order of the IDs.
   *
   * @param userId1 The first user ID (either tutor or student).
   * @param userId2 The second user ID (either tutor or student).
   * @return A string representing the consistent channel ID for the given users.
   */
  private fun generateChannelId(userId1: String, userId2: String): String {
    // Ensure the channel ID is the same regardless of the order of user IDs
    return listOf(userId1, userId2).sorted().joinToString("_")
  }

  /**
   * Sets the current active channel ID.
   *
   * This method updates the current active channel, allowing the UI to track which channel is
   * currently being used.
   *
   * @param channelId The ID of the channel to set as the current active channel. Can be null to
   *   indicate no active channel.
   */
  fun setCurrentChannelId(channelId: String?) {
    _currentChannelId.value = channelId
  }
}
