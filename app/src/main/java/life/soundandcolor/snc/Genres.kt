package life.soundandcolor.snc

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.GenresBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import life.soundandcolor.snc.utilities.ParseUtils
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class Genres : Fragment() {

    lateinit internal var myDb: DatabaseHelper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        StrictMode.enableDefaults()
        val binding: GenresBinding = DataBindingUtil.inflate(
                inflater, R.layout.genres, container, false)

        after = "0"
        myDb = DatabaseHelper(context)

        val res = myDb.get()
        res.moveToFirst()

        val user = res.getString(0)
        val token = res.getString(1)
        val refresh = res.getString(2)

        var genres = JSONObject()
        var ch = true
        while(ch) {
            var params = listOf(TYPE_PARAM to "artist", AFTER_PARAM to after, LIMIT_PARAM to limit)
            var jsonResponse = NetworkUtils.getRequest(BASE_URL2, params, token, refresh, myDb)
            var arr = ParseUtils.getSimpleStringsFromJson(context!!, jsonResponse!!, "Following", user)

            for (i in 0 until arr!!.length() - 1) {
                for (j in arr.getJSONObject(i).getString("genres").replace(Regex("[\\[\\]\"]"), "").split(","))
                    if (!j.equals("")) {
                        if (genres.has(j))
                            genres.put(j, genres.getInt(j) + 1)
                        else
                            genres.put(j, 1)
                    }
            }
            if (arr.length() % 50 ==0)
                after = arr.getJSONObject(arr.length() - 1).getString("id").toString()
            else
                ch = false
        }

        val listItems = ArrayList<String>()
        for (i in genres.keys())
            listItems.add(i + "  •  " + genres.getString(i))

        Collections.sort(listItems, object : Comparator<String> {
            override fun compare(a: String, b: String): Int {
                return -a.split("  •  ")[1].toInt().compareTo(b.split("  •  ")[1].toInt())
            }
        })

        Timber.e(genres.toString())
        val adapter = ArrayAdapter<String>(context, R.layout.simple, listItems.subList(0, 40))
        binding.list.adapter = adapter

        binding.list.setOnItemClickListener { parent, view, position, id ->
            val genre = listItems[position].split("  •  ").get(0)
            Toast.makeText(context, "$genre selected", Toast.LENGTH_SHORT).show()

            parent.findNavController().navigate(GenresDirections.actionGenresToContent(genre, "Following", "following"))
        }

        return binding.root
    }

    companion object {
        private val limit = "50"
        private var after = "0"

        internal val TYPE_PARAM = "type"
        internal val LIMIT_PARAM = "limit"
        internal val AFTER_PARAM = "after"

        private val BASE_URL2 = "https://api.spotify.com/v1/me/following"
    }
}