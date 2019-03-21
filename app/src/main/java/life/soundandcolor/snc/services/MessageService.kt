package life.soundandcolor.snc.services

import android.app.IntentService
import android.content.Intent
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