package com.github.se.project.model.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.se.project.model.profile.Profile
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {
  private val currentChannelID_ = MutableStateFlow<String?>(null)
  val currentChannelID = currentChannelID_.asStateFlow()

  val clientInitialisationState = chatClient.clientState.initializationState

  companion object {
    fun Factory(chatClient: ChatClient): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatViewModel(chatClient) as T
          }
        }
  }

  fun connect(profile: Profile) {
    val user =
        User(
            id = profile.uid,
            name = "${profile.firstName} ${profile.lastName}",
        )
    val token = chatClient.devToken(user.id)
    chatClient.connectUser(user, token).enqueue()
  }

  fun setChannelID(channelID: String?) {
    currentChannelID_.value = channelID
  }
}
