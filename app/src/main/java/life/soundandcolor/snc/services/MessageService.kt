package life.soundandcolor.snc.services

import android.app.IntentService
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.github.kittinunf.fuel.Fuel
import life.soundandcolor.snc.RealNotifications
import timber.log.Timber

class MessageService : IntentService("MessageService") {

    override fun onHandleIntent(workIntent: Intent) {

        val dataString = workIntent.dataString
        // Do work here, based on the contents of dataString
        Timber.e(dataString)
//        val (_, response, _) = Fuel.get_current(dataString)
//                .response()
    }
}