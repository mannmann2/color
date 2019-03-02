package life.soundandcolor.snc

import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import life.soundandcolor.snc.databinding.ContentBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import life.soundandcolor.snc.utilities.ParseUtils
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class Content : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    lateinit internal var contentAdapter: ContentAdapter

    private var mErrorMessageDisplay: TextView? = null
    private var mLoadingIndicator: ProgressBar? = null
    var loadMore: Boolean = false
//    private var jsonResponse: String? = null
    private var simpleData: Array<String>? = null
    private var id: String? = null
    private var extra: String? = null
    private var filter: String? = null
    private  var base: String? = null
    lateinit internal var title: String
    lateinit internal var user: String
    lateinit internal var url: String
    lateinit internal var params: List<Pair<String, String>>
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var res: Cursor
    private var curMenuItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: ContentBinding = DataBindingUtil.inflate(
                inflater, R.layout.content, container, false)

        offset = 0
        limit1 = 49
        after = "0"

        var args = ContentArgs.fromBundle(arguments)
        id = args.id
        extra = args.extra
        filter = args.filter

        myDb = DatabaseHelper(context)
        res = myDb.get()
        res.moveToFirst()
        user = res.getString(0)

        mRecyclerView = binding.recyclerviewForecast
        mErrorMessageDisplay = binding.errorMessageDisplay

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mRecyclerView!!.setLayoutManager(layoutManager)

        contentAdapter = ContentAdapter(id!!, res.getString(0))
        mRecyclerView!!.adapter = contentAdapter

        if (!filter.equals("0")) { // woah calm down bro this should not be here
            val arr = JSONArray()
            val res2 = myDb.get2("following", "where user = '$user'")
            var i = 0
            while (res2.moveToNext()) {
                val genres = res2.getString(7)
                if (genres.contains("\""+filter!!+"\"")) {
                    val temp = JSONObject()
                    temp.put("name", res2.getString(1))
                    temp.put("url", res2.getString(3))
                    temp.put("pop", res2.getString(6))
                    temp.put("img", res2.getString(4))
                    temp.put("type", res2.getString(5))
                    Timber.e(temp.toString())
                    arr.put(i, temp)
                    i += 1
                }
            }

            val data = Array<String>(arr!!.length()) { arr.optString(it) }
            /*
             * Use this setting to improve performance if you know that changes in content do not
            * change the child layout size in the RecyclerView
            */
            mRecyclerView!!.setHasFixedSize(true)
            showWeatherDataView()
            contentAdapter.setData(data)
            title = filter!!

        }
        else {
            mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = recyclerView.layoutManager!!.itemCount
                    if (mLoadingIndicator!!.visibility == View.INVISIBLE && totalItemCount > 13 &&
                            totalItemCount <= layoutManager.findLastVisibleItemPosition() + 2) {
                        if (id == "Following" || id == "Saved Tracks" || id == "Saved Albums") {
                            if (totalItemCount % 50 == 0) {
                                contentAdapter.loading()
                                when (id) {
                                    "Following" -> {
                                        after = JSONObject(simpleData!![simpleData!!.size - 1]).getString("id").toString()
                                    }
                                    "Saved Tracks", "Saved Albums" -> {
                                        offset = totalItemCount
                                    }
                                    //                                "Recent" -> {
                                    //                                    contentAdapter.loading()
                                    //                                    val json = JSONObject(jsonResponse)
                                    //                                    before = json.getJSONObject("cursors").getString("before")
                                    //                                    loadMore = true
                                    //                                    loadWeatherData()
                                    //                                }
                                }
                                loadMore = true
                                loadWeatherData()
                            } else if (!loadMore)
                                Toast.makeText(context, "No more data", Toast.LENGTH_SHORT).show()
                        } else if ((id == "Top Artists" || id == "Top Tracks") && totalItemCount == 49) {
                            contentAdapter.loading()
                            offset = 49
                            limit1 = 50
                            loadMore = true
                            loadWeatherData()
                        }
                    }
                }
            })

            mLoadingIndicator = binding.pbLoadingIndicator
            //        (activity as AppCompatActivity).supportActionBar?.title = res.getString(2)+": "+id
            title = id!!

            if (!loadMore)
                loadWeatherData()
        }
        (activity as AppCompatActivity).supportActionBar!!.title = title
        (activity as AppCompatActivity).supportActionBar!!.isHideOnContentScrollEnabled = true
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun loadWeatherData() {
        showWeatherDataView()
        Fetch().execute(extra)
    }

    private fun showWeatherDataView() {
        mErrorMessageDisplay!!.visibility = View.INVISIBLE
        mRecyclerView!!.setVisibility(View.VISIBLE)
        curMenuItem?.setVisible(true)
    }

    private fun showErrorMessage() {
        mRecyclerView!!.setVisibility(View.INVISIBLE)
        mErrorMessageDisplay!!.visibility = View.VISIBLE
    }

    inner class Fetch : AsyncTask<String, Void, Array<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (!loadMore)
                mLoadingIndicator!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg para: String): Array<String>? {

            if (para.size == 0) {
                return null
            }

            val token = res.getString(1)
            val refresh = res.getString(2)
            Timber.e("User " + user)

            if (extra == "following") {
                url = BASE_URL2
                params = listOf(AFTER_PARAM to after!!, LIMIT_PARAM to Integer.toString(limit))
//                params = listOf(AFTER_PARAM to after!!, "token" to token, "username" to user)
            }
            else if (extra == "recent") {
                url = BASE_URL3
                params = listOf(LIMIT_PARAM to Integer.toString(limit))

//                if (before != null) {
//                    builtUri = temp.appendQueryParameter(BEFORE_PARAM, before).build()
//                    before = null
//                }
//                else {
//                    builtUri = temp.build()
//                }
            }
            else if ((extra == "tracks") || (extra == "albums")) {
                url = BASE_URL + extra
                params = listOf(OFFSET_PARAM to Integer.toString(offset), LIMIT_PARAM to Integer.toString(limit))
            }
            else {
                if (id!! == "Top Artists")
                    base = "artists"
                else
                    base = "tracks"

                url = BASE_URL+"top/"+base
                params = listOf(TIME_PARAM to extra!!, LIMIT_PARAM to Integer.toString(limit1),
                        OFFSET_PARAM to Integer.toString(offset))
            }

            try {
                val jsonResponse = NetworkUtils.getRequest(url, params, token, refresh, myDb)
                var arr = ParseUtils.getSimpleStringsFromJson(context!!, jsonResponse!!, id!!, user)!!
//                if (extra == "recent") {
//                    arr = JSONArray()
//                    val rec = myDb.get2("feed")
//                    while (rec.moveToNext()) {
//                        if (rec.getString(0) == user) {
//                            var temp = JSONObject()
//                            for (j in 0..9)
//                                temp.put(rec.getColumnName(j), rec.getString(j))
//                            Timber.e(temp.toString())
//                            arr.put(temp)
//                        }
//                    }
//                }
                simpleData = Array<String>(arr!!.length()) { arr.optString(it) }
                return simpleData

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(data: Array<String>?) {
            mLoadingIndicator!!.visibility = View.INVISIBLE
            if (data != null) {
                showWeatherDataView()
                if (loadMore) {
                    contentAdapter.addData(data)
                    loadMore = false
                }
                else
                    contentAdapter.setData(data)
            } else {
                showErrorMessage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if ((id == "Top Tracks") || (id == "Recent"))
            inflater?.inflate(R.menu.analysis_menu, menu)
            curMenuItem = menu?.findItem(R.id.Analysis)
            curMenuItem?.setVisible(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.Analysis -> {
                var ids = ""
                for (item in simpleData!!) {
                    ids += JSONObject(item).getString("id") + ","
                }
//                val idBundle = Bundle()
//                idBundle.putString("nameArg", jsonResponse.toString())
                this.findNavController().navigate(ContentDirections.actionContentToAnalysis(ids.substring(0, ids.length -1), "content"))
            }
        }
        return super.onOptionsItemSelected(item)
//        return NavigationUI.onNavDestinationSelected(item!!,
//                view!!.findNavController()) || super.onOptionsItemSelected(item)
    }

//    fun onOptionsItemSelected(item: MenuItem, linearLayout: LinearLayout): Boolean {
//        when (item.itemId ) {
//            R.id.share -> {
//                val shareBody = "Welcome to sound & color."
//                val shareIntent = Intent(Intent.ACTION_SEND)
//                shareIntent.setType("text/plain")
//                        .putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
//                        .putExtra(Intent.EXTRA_TEXT, shareBody)
//                startActivity(shareIntent)
////                startActivity(Intent.createChooser(shareIntent, "Share via"))
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    companion object {
        private var IP = "http://13.233.83.58:5000"
        private val limit = 50
        private var limit1 = 49
        private var offset = 0
        private var after: String? = null
//        private var before: String? = null

        internal val LIMIT_PARAM = "limit"
        internal val OFFSET_PARAM = "offset"
        internal val AFTER_PARAM = "after"
//        internal val BEFORE_PARAM = "before"
        internal val TIME_PARAM = "time_range"

        private val BASE_URL = "https://api.spotify.com/v1/me/"
        private val BASE_URL2 = "https://api.spotify.com/v1/me/following?type=artist"
//        private val BASE_URL2 = IP + "/api/v1/following"
        private val BASE_URL3 = "https://api.spotify.com/v1/me/player/recently-played"
    }
}