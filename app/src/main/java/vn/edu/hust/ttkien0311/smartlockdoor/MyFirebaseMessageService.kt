package vn.edu.hust.ttkien0311.smartlockdoor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "vn.edu.hust.ttkien0311.smartlockdoor"

class MyFirebaseMessageService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            val title: String = remoteMessage.notification!!.title!!
            val body: String = remoteMessage.notification!!.body!!
            generateNotification(title, body)
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("SLD", "Message data payload: ${remoteMessage.data}")
            val dataPayload = remoteMessage.data
            val title: String = dataPayload["title"].toString()
            val body: String = dataPayload["body"].toString()
            generateNotification(title, body)
        }

    }

    private fun generateNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}