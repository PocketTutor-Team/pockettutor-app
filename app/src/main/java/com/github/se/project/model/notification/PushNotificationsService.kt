package com.github.se.project.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.se.project.R
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.ProfilesRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class PushNotificationsService : FirebaseMessagingService() {
  private val profilesRepository: ProfilesRepository =
      ProfilesRepositoryFirestore(FirebaseFirestore.getInstance())

  override fun onNewToken(token: String) {
    super.onNewToken(token)

    Log.d("FCM", "New token generated: $token")

    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
      val userUid = currentUser.uid

      // Update token in the Firestore database
      profilesRepository.updateToken(
          uid = userUid,
          newToken = token,
          onSuccess = { Log.d("FCM", "Token updated successfully for user: $userUid") },
          onFailure = { exception ->
            Log.e("FCM", "Failed to update token for user: $userUid", exception)
          })
    } else {
      Log.w("FCM", "No authenticated user found; token not updated.")
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    // Log.d("FCM", "Message received: ${remoteMessage.data}")

    // Show notification
    val title = remoteMessage.notification?.title ?: "Default Title"
    val message = remoteMessage.notification?.body ?: "Default Message"
    showNotification(title, message)
  }

  private fun showNotification(title: String, message: String) {
    val channelId = "default_channel_id"
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
          NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(channel)
    }

    val notification =
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logopocket)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    notificationManager.notify(0, notification)
  }

  fun sendNotificationToUser(token: String, title: String, body: String) {
    // Example FCM endpoint
    val url = "https://fcm.googleapis.com/fcm/send"

    // Replace with your Firebase Server Key
    val serverKey = "YOUR_SERVER_KEY"

    // Build JSON payload
    val json = JSONObject()
    json.put("to", token)
    json.put(
        "notification",
        JSONObject().apply {
          put("title", title)
          put("body", body)
        })

    // Make HTTP request
    val client = OkHttpClient()
    val requestBody = RequestBody.create("application/json".toMediaType(), json.toString())
    val request =
        Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "key=$serverKey")
            .addHeader("Content-Type", "application/json")
            .build()

    client.newCall(request).execute().use { response ->
      if (response.isSuccessful) {
        println("Notification sent successfully: ${response.body?.string()}")
      } else {
        println("Failed to send notification: ${response.body?.string()}")
      }
    }
  }
}
