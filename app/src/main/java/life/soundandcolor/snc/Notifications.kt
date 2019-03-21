package life.soundandcolor.snc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

interface Notifications {

//    fun updateNotification(mediaSession: MediaSessionCompat)

    fun buildNotification(): Notification
    fun buildNotification2(): Notification
}

class RealNotifications(
        private val context: Context
//        private val notificationManager: NotificationManager
    ) : Notifications {

    override fun buildNotification(): Notification {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        createNotificationChannel()

        var builder = NotificationCompat.Builder(context, "CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("notifications")
                .setContentText("text")
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit onubfuygfogouyaegfuyfgouyfghoe8fgos8fvo87sgrvh8e7rhve8r7vbpe87gv8a7rgv87arvh87ergvueyrvb87ergvb8e7gvoe8a7gvo8e7vae87rvo8ae7rvheao8r7v8e7rvh8e7vboa8ee line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        return builder.build()
    }

//    override fun buildNotification2(): Notification {
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val KEY_TEXT_REPLY = "key_text_reply"
//        var replyLabel: String = "RR"
//        var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
//            setLabel(replyLabel)
//            build()
//        }
//
//        // Build a PendingIntent for the reply action to trigger.
//        var replyPendingIntent: PendingIntent =
//                PendingIntent.getBroadcast(context,
//                        0,
//                        intent,
////                        getMessageReplyIntent(conversation.getConversationId()),
//                        PendingIntent.FLAG_UPDATE_CURRENT)
//
//        var action: NotificationCompat.Action =
//                NotificationCompat.Action.Builder(R.drawable.ic_add_black,
//                        "RV", replyPendingIntent)
////                        .addRemoteInput(remoteInput)
//                        .build()
//
//        val newMessageNotification = NotificationCompat.Builder(context, "CHANNEL")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("notifyyyy")
//                .setContentText("new friends")
//                .addAction(action)
//
//        return newMessageNotification.build()
//    }

    override fun buildNotification2(): Notification {
        val snoozeIntent = Intent(context, MainActivity::class.java).apply {
//            action = ACTION_SNOOZE
//            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val snoozePendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context, 0, snoozeIntent, 0)

        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, "CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_chat_black, "ACCEPT",
                        snoozePendingIntent)
                .addAction(R.drawable.ic_home_black, "REJECT",
                        snoozePendingIntent)
                .setAutoCancel(true)

        return builder.build()
    }

    fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}