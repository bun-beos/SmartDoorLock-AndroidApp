package vn.edu.hust.ttkien0311.smartlockdoor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.MainActivity
import vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome.WelcomeActivity

class MyFirebaseMessageService : FirebaseMessagingService() {
//    private val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if (sharedPreferencesManager.getLoginStatus()) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.d("SLD", "Firebase message data payload: ${remoteMessage.data}")
                val dataPayload = remoteMessage.data
                val title = dataPayload["title"].toString()
                val body = dataPayload["body"].toString()
                val id = dataPayload["notifId"].toString()
                val doorState = dataPayload["doorState"].toString()
                generateNotification(title, body, id, doorState)
            }
//        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SLD", "Firebase new token: $token")
    }

    private fun generateNotification(title: String, body: String, id: String, doorState: String) {
        val channelId = resources.getString(R.string.default_notification_channel_id)
        val channelName = resources.getString(R.string.default_notification_channel_name)

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("fragmentToLoad", "NotificationDetailFragment")
        intent.putExtra("NotifId", id)
        intent.putExtra("DoorState", doorState)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.app_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}