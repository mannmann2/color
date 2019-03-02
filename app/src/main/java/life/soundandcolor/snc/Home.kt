package life.soundandcolor.snc

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity
import life.soundandcolor.snc.databinding.HomeBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import life.soundandcolor.snc.utilities.ParseUtils
import org.json.JSONObject
import org.json.JSONException
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class Home : Fragment() {

//    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11
//    private var mAccessToken: String? = null
    private var mAccessCode: String? = null

    private var mRecyclerView: RecyclerView? = null
    private var layoutManager: LinearLayoutManager? = null
    lateinit internal var feedAdapter: FeedAdapter

    private var mErrorMessageDisplay: TextView? = null
    private var mLoadingIndicator: ProgressBar? = null
    var loadMore: Boolean = false
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var res: Cursor
    private var curMenuItem: MenuItem? = null
    private var lin1: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: HomeBinding = DataBindingUtil.inflate(
                inflater, R.layout.home, container, false)

        myDb = DatabaseHelper(context)
        val cur = myDb.get()
        cur.moveToNext()

        lin1 = binding.lin1
        mRecyclerView = binding.recyclerview2

        if (myDb.check().count == 1) {
            lin1!!.setVisibility(View.GONE)
            mRecyclerView!!.setVisibility(View.VISIBLE)
        }
        else {
            lin1!!.setVisibility(View.VISIBLE)
            mRecyclerView!!.setVisibility(View.GONE)
        }

        binding.login.setOnClickListener { v: View -> onRequestTokenClicked() }

        binding.fab1.setOnClickListener { v: View ->
            v.findNavController().navigate(HomeDirections.actionHomeToProfile())
        }

//        mErrorMessageDisplay = binding.errorMessageDisplay

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mRecyclerView!!.setLayoutManager(layoutManager)

        val feed_data = get_feed()

        feedAdapter = FeedAdapter(cur.getString(1)) {

//            val friends = Helper.getFriends(DatabaseHelper(context))
//            val listItems = ArrayList<String>()
//            while (friends.moveToNext())
//                listItems.add(friends.getString(0))
//            val adapter = ArrayAdapter<String>(context, R.layout.simple, listItems)
//            binding.send2.adapter = adapter
//            binding.send2.visibility = View.VISIBLE
//
//            val transaction = activity!!.supportFragmentManager.beginTransaction()
//            transaction.add(R.id.myNavHostFragment, Share())
//            transaction.addToBackStack(null)
//            transaction.commit()
            findNavController().navigate(HomeDirections.actionHomeToShare(feed_data[it].toString()))
        }

        mRecyclerView!!.adapter = feedAdapter
//        mRecyclerView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }

//        mLoadingIndicator = binding.pbLoadingIndicator
//        var data = Array<String>(arr!!.length()) {arr.optString(it)}
        (activity as AppCompatActivity).supportActionBar?.title = "Feed"
        (activity as AppCompatActivity).supportActionBar!!.isHideOnContentScrollEnabled = true

        feedAdapter.setData(feed_data)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.refresh) {
            Fetch().execute()
        }
        else if (item.itemId == R.id.chats) {
            findNavController().navigate(HomeDirections.actionHomeToChats())
        }
        else {
            return NavigationUI.onNavDestinationSelected(item!!,
                    view!!.findNavController()) || super.onOptionsItemSelected(item)
        }
        return true
    }


    fun get_feed(): ArrayList<JSONObject> {
        var data = ArrayList<JSONObject>()
        res = myDb.get2("feed")
        while (res.moveToNext()) {
            val temp = JSONObject()
            for (i in 0..9)
                temp.put(res.getColumnName(i), res.getString(i))
            data.add(temp)
        }

        Collections.sort(data, object : Comparator<JSONObject> {
            override fun compare(a: JSONObject, b: JSONObject): Int {
                var valA = String()
                var valB = String()

                try {
                    valA = a.getString("timestamp")
                    valB = b.getString("timestamp")
                } catch (e: JSONException) {}
                return -valA.compareTo(valB)
            }
        })
        return data
    }

    inner class Fetch : AsyncTask<String, Void, Array<String>>() {

//        override fun doInBackground(vararg params: String): Array<String>? {
//            res = myDb.get2("users")
//            while (res.moveToNext())
//                Fetch2(res.getString(0), res.getString(1), res.getString(2)).execute()
//
//            return null
//        }
//
//        override fun onPostExecute(data: Array<String>?) {
//            feedAdapter.setData(get_feed())
//            layoutManager!!.scrollToPositionWithOffset(0, 0);
//        }
//    }
//
//    inner class Fetch2(user: String, token:String, refresh:String) : AsyncTask<String, Void, Array<String>>() {
//
//        val user = user
//        val token = token
//        val refresh = refresh

        override fun doInBackground(vararg params: String): Array<String>? {
            res = myDb.get2("users")
            while (res.moveToNext()) {

                val user = res.getString(0)
                val token = res.getString(1)
                val refresh = res.getString(2)
                Timber.e("User " + user)

                try {
                    val jsonResponse = NetworkUtils.getRequest(BASE_URL3, listOf(LIMIT_PARAM to Integer.toString(limit)), token, refresh, myDb)
                    ParseUtils.getSimpleStringsFromJson(context!!, jsonResponse!!, "Recent", user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }

        override fun onPostExecute(data: Array<String>?) {
            feedAdapter.setData(get_feed())
            layoutManager!!.scrollToPositionWithOffset(0, 0);
        }
    }

        companion object {
            private val limit = 50

            internal val LIMIT_PARAM = "limit"
            internal val AFTER_PARAM = "after"
//            internal val BEFORE_PARAM = "before"

            private val BASE_URL3 = "https://api.spotify.com/v1/me/player/recently-played"
        }




    /* --------------------------------------- LOGIN STUFF --------------------------------------------- */


    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(getString(R.string.CLIENT_ID), type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(arrayOf("user-read-email","user-top-read","user-follow-read","user-library-read",
                        "user-read-recently-played","user-read-email","user-read-currently-playing","user-read-playback-state",
                        "user-modify-playback-state","streaming","playlist-modify-private","playlist-modify-public"))
                .setCampaign("your-campaign-token")
                .build()
    }

    fun createLoginActivityIntent(request: AuthenticationRequest): Intent {
        val intent = LoginActivity.getAuthIntent(activity, request)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    fun onRequestTokenClicked() {
        val request = getAuthenticationRequest(AuthenticationResponse.Type.CODE)
//        AuthenticationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request)
        val intent = createLoginActivityIntent(request)
        startActivityForResult(intent, AUTH_CODE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthenticationClient.getResponse(resultCode, data)

//        if (AUTH_CODE_REQUEST_CODE == requestCode) {
//            mAccessToken = response.accessToken
        mAccessCode = response.code

        val params = listOf("grant_type" to "authorization_code",
                            "code" to mAccessCode!!,
                            "redirect_uri" to getString(R.string.REDIRECT_URI),
                            "client_id" to getString(R.string.CLIENT_ID),
                            "client_secret" to getString(R.string.CLIENT_SECRET))

        var (_, res, _) = Fuel.post("https://accounts.spotify.com/api/token", params).response()

        var content = JSONObject(res.body().asString("application/json"))
        val token = content.getString("access_token")
        val refresh = content.getString("refresh_token")
        Timber.e("token " + token)
        Timber.e("refresh " + refresh)

        val json = NetworkUtils.getRequest("https://api.spotify.com/v1/me", null, token, refresh, myDb)
        val username = json!!.getString("id")

        val kkk = JSONObject()
        kkk.put("username", username)
        kkk.put("token", token)
        kkk.put("refresh", refresh)
        kkk.put("name", json.getString("display_name"))
        kkk.put("email", json.getString("email"))
        kkk.put("uri", json.getString("uri"))
        kkk.put("owner", true)

        Timber.e(kkk.toString())
        myDb.add(kkk, myDb.writableDatabase, "users")
        myDb.change(username)

        lin1!!.setVisibility(View.GONE)
        mRecyclerView!!.setVisibility(View.VISIBLE)
        fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
//        }
    }

    fun tokenClicked() {
        val user = "mannmann2"
        val kkk = JSONObject()
        kkk.put("username", user)
        kkk.put("token", "AQAb0eqxlZ56Eb7oTfIFon4KjPsCKNxyoakZvYakvvtCaQd6i3jQ6YyK3381XciPB0nO8a0PcmoIQxFsC9Lm90MrkUV6oWFPTJQXo3XeKmL1R5-U_g229Cu1O-OD6V43Ijk")
        kkk.put("refresh", "BQAKpySgbM54IJx8aqz3SuXIUHyyeR3gZl0tzuDWCH_NKavpbtH1VEVNddnNQk5KbhckfWBC0QY0SDwFKvZwb1u0hvbNXo3nCDnWYmLa0Sj2Lz7r3p_iSt-1AHrAn70lETTk7zXX9YpI1t1gNaKszXzauoOA4lcO0FXRJdm_nbLkmz8xxfNW7KHmP6xvdf4MV7J3aH76c45RHMEjBgbbdfTkE5tseuz8n4dzEkSnzhuwwh8ow0g-z7-qjNv609tDLajRB-5mrPiLuA")
        kkk.put("owner", true)
        Timber.e(kkk.toString())
        myDb.add(kkk, myDb.writableDatabase, "users")
        myDb.change(user)

        lin1!!.setVisibility(View.GONE)
        mRecyclerView!!.setVisibility(View.VISIBLE)
        fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build()
    }

    fun share() {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.add(R.id.myNavHostFragment, Users(), "tag")
//        supportActionBar?.title = "Share"
        transaction.addToBackStack(null)
        transaction.commit()
    }
}