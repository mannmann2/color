package life.soundandcolor.snc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import life.soundandcolor.snc.utilities.NetworkUtils
import org.json.JSONArray

class StatsViewModel : ViewModel() {

    val timeline = MutableLiveData<JSONArray>()
    val artists = MutableLiveData<ArrayList<String>>()
    val tracks = MutableLiveData<ArrayList<String>>()
    val trending_tracks_day = MutableLiveData<ArrayList<String>>()
    val trending_tracks_week = MutableLiveData<ArrayList<String>>()
    val trending_artists_day = MutableLiveData<ArrayList<String>>()
    val trending_artists_week = MutableLiveData<ArrayList<String>>()

    fun select(owner: String) {
        val js = loadData(owner)

        val t1 = ArrayList<String>()
        val js1 = js.getJSONArray(1)
        for (i in 0 until js1.length()) {
            var js11 = js1.getJSONObject(i)
            var count = js11.getString("doc_count")
            if (count.toInt() > 2)
                t1.add(js11.getString("key") + "  •  " + count)
        }

        val t2 = ArrayList<String>()
        val js2 = js.getJSONArray(2)
        for (i in 0 until js2.length()) {
            var js22 = js2.getJSONObject(i)
            var count = js22.getString("doc_count")
            if (count.toInt() > 2)
                t2.add(js22.getString("key") + "  •  " + count)
        }

        val t3 = ArrayList<String>()
        val js3 = js.getJSONArray(3)
        for (i in 0 until js3.length()) {
            var js33 = js3.getJSONObject(i)
            var count = js33.getString("doc_count")
            if (count.toInt() > 2)
                t3.add(js33.getString("key") + "  •  " + count)
        }

        val t4 = ArrayList<String>()
        val js4 = js.getJSONArray(4)
        for (i in 0 until js4.length()) {
            var js44 = js4.getJSONObject(i)
            var count = js44.getString("doc_count")
            if (count.toInt() > 2)
                t4.add(js44.getString("key") + "  •  " + count)
        }

        val t5 = ArrayList<String>()
        val js5 = js.getJSONArray(5)
        for (i in 0 until js5.length()) {
            var js55 = js5.getJSONObject(i)
            var count = js55.getString("doc_count")
            if (count.toInt() > 2)
                t5.add(js55.getString("key") + "  •  " + count)
        }

        val t6 = ArrayList<String>()
        val js6 = js.getJSONArray(6)
        for (i in 0 until js6.length()) {
            var js66 = js6.getJSONObject(i)
            var count = js66.getString("doc_count")
            if (count.toInt() > 2)
                t6.add(js66.getString("key") + "  •  " + count)
        }

        timeline.value = js.getJSONArray(0)

        artists.value = t1
        tracks.value = t2
        trending_tracks_day.value = t3
        trending_tracks_week.value = t4
        trending_artists_day.value = t5
        trending_artists_week.value = t6

    }

//    fun getUsers(): LiveData<JSONArray> {
//        return users
//    }

    private fun loadData(owner: String) : JSONArray {
        val js = JSONArray(NetworkUtils.getRequest("stats", listOf("username" to owner)))
        return js
    }
}