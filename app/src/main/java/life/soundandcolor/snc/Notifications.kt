package life.soundandcolor.snc

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

interface Notifications {

//    fun updateNotification(mediaSession: MediaSessionCompat)

    fun buildNotification(): Notification
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

        var builder = NotificationCompat.Builder(context!!, "CHANNEL")
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