package com.nb.trackerapp.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nb.trackerapp.R
import com.nb.trackerapp.base.AppSession
import java.util.*

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        AppSession.setDeviceToken(this,p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        generateNotification(this,p0)
    }

    private fun generateNotification(context: Context,remoteMessage: RemoteMessage){
        Log.d("response","fcm msg : ${remoteMessage.data}")
        val body:String? = remoteMessage.data["body"]
        val title:String? = remoteMessage.data["title"]

        // --- Creating notification ---
        val channelId = "trackerApp"
        val notificationID = getId()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "trackerNotification"
            val notificationDescription = "tracker_notification_description"
            val notificationChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            notificationChannel.description = notificationDescription
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),audioAttributes)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context,R.color.transparent))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationID, builder.build())
    }

    private fun getId():Int{
        return Random().nextInt()
    }
}