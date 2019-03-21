package life.soundandcolor.snc

import android.database.Cursor
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import life.soundandcolor.snc.databinding.AnalysisBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class Analysis : Fragment() {

    lateinit internal var myDb: DatabaseHelper
    lateinit internal var res: Cursor
    lateinit internal var ids: String
    lateinit internal var from: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        StrictMode.enableDefaults()
        val binding: AnalysisBinding = DataBindingUtil.inflate(
                inflater, R.layout.analysis, container, false)

        var args = AnalysisArgs.fromBundle(arguments)
        ids = args.ids
        from = args.from

        myDb = DatabaseHelper(context)
        res = myDb.get_owner()

        val user = res.getString(0)
//        val token = res.getString(1)
//        val refresh = res.getString(2)

//        var jsonResponse = NetworkUtils.getRequest("https://api.spotify.com/v1/audio-features",
//                      listOf("usernames" to usernames), token, refresh, myDb)
//        var json = jsonResponse!!.getJSONArray("audio_features")
        var json = JSONArray(NetworkUtils.getRequest("features", listOf("username" to user, "ids" to ids)))
        var L = json.length()

        var keys = listOf("danceability", "energy", "loudness", "speechiness", "acousticness", "instrumentalness", "liveness", "valence", "mode", "tempo", "duration_ms")
        var D = JSONObject()
        for (key in keys)
            D.put(key, 0)

        for (i in 0 until L) {
            val item = json.getJSONObject(i)
            for (key in item.keys())
                if (key in keys)
                    D.put(key, D.getDouble(key) + (item.getDouble(key))/L)
        }
        Timber.e(D.toString())

        var text = ""
        for (key in D.keys()) {
            if (key == "duration_ms")
                text += "average duration:  " + String.format("%.2f", D.getString(key).toFloat()/60000) +" mins\n"
            else if (key == "tempo")
                text += "average tempo:  " + String.format("%.2f", D.getString(key).toFloat()) + "\n"
            else if (key == "loudness")
                text += "loudness:  " + String.format("%.2f", (D.getString(key).toFloat() + 60)/60*100) + "%\n"
            else
                text += key + ":  " + String.format("%.2f", D.getString(key).toFloat()*100) + "%\n"
        }
        binding.analysisText.text = text

        if (from == "feed") {
//            jsonResponse = NetworkUtils.getRequest("https://api.spotify.com/v1/recommendations",
//                    listOf("seed_tracks" to usernames), token, refresh, myDb)
//            var json = jsonResponse!!.getJSONArray("tracks")
            json = JSONArray(NetworkUtils.getRequest("recommendations",
                    listOf("username" to user, "seed_tracks" to ids)))

            val listItems = ArrayList<String>()
            for (i in 0 until json.length()) {
                var js = json.getJSONObject(i)
                listItems.add(js.getString("name") + "  •  " +
                        js.getJSONObject("album").getString("name") + "  •  " +
                        js.getJSONArray("artists").getJSONObject(0).getString("name"))
        }

        val adapter = ArrayAdapter<String>(context, R.layout.simple, listItems)
        binding.list.adapter = adapter

        }
        return binding.root
    }
}