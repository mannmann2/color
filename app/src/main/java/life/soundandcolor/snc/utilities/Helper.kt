package life.soundandcolor.snc.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*


object Helper {

    //    public String get_time(int ms) {
    //        int dur = ms/1000;
    //        int sec = dur%60;
    //        dur = dur/60;
    //        String time = '%d:%02d'%(dur, sec);
    //        return time;
    //    }

    fun play(view: View) {
//        log play type X by Y on Z(feed, top tracks etc)
//        Fuel.put("https://api.spotify.com/v1/me/player/play", listOf(""))
//                .header("Authorization", "Bearer " + token)
        val context = view.context
        val uri = Uri.parse(view.tag.toString())
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)

//        val detailsIntent = Intent(this, DetailsActivity::class.java)

//        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context)
//                .addNextIntentWithParentStack(intent)
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        pendingIntent!!.send()

//
//        val builder = NotificationCompat.Builder(context)
//                .setContentIntent(pendingIntent)
    }

    fun share(context: Context, shareBody: String, uri: String) {
        // log share of ABC type X by Y on Z maybe as a service?
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                .putExtra(Intent.EXTRA_TEXT, shareBody+"\n" + uri + "\n\n- via Sound & Color")

        context.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun parseMessage(message: String): String {
        if (("https://open.spotify.com" in message) && !("url\":\"http" in message)) {
            val js = JSONObject()
            js.put("url", message)
            js.put("type", "artist")
            Jsoup.connect(message).get().run {
                select("meta").forEachIndexed { index, element ->
                    if (element.attr("property") == "og:title")
                        js.put("name", element.attr("content"))
                    else if (element.attr("property") == "og:description")
                        js.put("pop", element.attr("content"))
                    else if (element.attr("property") == "og:image") {
                        js.put("img",element.attr("content"))
                    }
                }
            }
            js.put("url", message)

            return js.toString()
        }
        else return message
    }

    fun displayTime(timestamp: String): String {
        val cal = Calendar.getInstance()
        val ms = timestamp.toLong()
        cal.timeInMillis = ms
        cal.add(Calendar.MILLISECOND, TimeZone.getDefault().getOffset(ms));
        val formatter = SimpleDateFormat("dd MMM, yyyy 'at' hh:mma")
        return formatter.format(cal.time)
    }

    fun toTimestamp(date: String): Long {
        var formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        try{
            return formatter.parse(date).getTime()
        }
        catch (e: Exception) {
            formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            return formatter.parse(date).getTime()
        }
    }
}