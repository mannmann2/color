package life.soundandcolor.snc

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
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
import java.util.Collections
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
    private var login: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        StrictMode.enableDefaults()
        val binding: HomeBinding = DataBindingUtil.inflate(
                inflater, R.layout.home, container, false)

        myDb = DatabaseHelper(context)

        login = binding.login
        mRecyclerView = binding.recyclerview2
        if (myDb.check().count == 1) {
            login!!.setVisibility(View.GONE)
            mRecyclerView!!.setVisibility(View.VISIBLE)
        }
        else {
            login!!.setVisibility(View.VISIBLE)
            mRecyclerView!!.setVisibility(View.GONE)
        }

        binding.loginButton.setOnClickListener { v: View -> onRequestTokenClicked() }

        binding.fab1.setOnClickListener { v: View ->
            v.findNavController().navigate(HomeDirections.actionHomeToProfile())
        }

//        mErrorMessageDisplay = binding.errorMessageDisplay

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mRecyclerView!!.setLayoutManager(layoutManager)

        val feed_data = get_feed()
        feedAdapter = FeedAdapter() {

//            val friends = Helper.getFriends(DatabaseHelper(context))
//            val trending = ArrayList<String>()
//            while (friends.moveToNext())
//                trending.add(friends.getString(0))
//            val adapter = ArrayAdapter<String>(context, R.layout.simple, trending)
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
            return NavigationUI.onNavDestinationSelected(item,
                    view!!.findNavController()) || super.onOptionsItemSelected(item)
        }
        return true
    }


    fun get_feed(): ArrayList<JSONObject> {
        var data = ArrayList<JSONObject>()
        res = myDb.get("feed")
        while (res.moveToNext()) {
            val temp = JSONObject()
            for (i in 0..10)
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
//            res = myDb.get("users")
//            while (res.moveToNext())
//                Fetch2(res.getString(0), res.getString(1), res.getString(2)).execute()
//
//            return null
//        }
//        override fun onPostExecute(data: Array<String>?) {
//            feedAdapter.setData(get_feed())
//            layoutManager!!.scrollToPositionWithOffset(0, 0);
//        }
//    }
//
//    inner class Fetch2(username: String, token:String, refresh:String) : AsyncTask<String, Void, Array<String>>() {
//
//        val username = username
//        val token = token
//        val refresh = refresh

        override fun doInBackground(vararg params: String): Array<String>? {
            res = myDb.get("users")
            while (res.moveToNext()) {

                val user = res.getString(0)
//                val token = res.getString(1)
//                val refresh = res.getString(2)
                Timber.e("User " + user)

                try {
//                    val jsonResponse = NetworkUtils.getRequest("https://api.spotify.com/v1/me/player/recently-played",
//                            listOf("limit" to Integer.toString(limit)), token, refresh, myDb)
                    val jsonResponse = JSONObject(NetworkUtils.getRequest("recently-played",
                            listOf("username" to user)))
                    ParseUtils.getSimpleStringsFromJson(context!!, jsonResponse, "Recent", user, res.getString(1))
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

        internal val AFTER_PARAM = "after"
//            internal val BEFORE_PARAM = "before"
    }


    /* --------------------------------------- LOGIN STUFF --------------------------------------------- */


    private fun getAuthenticationRequest(type: AuthenticationResponse.Type): AuthenticationRequest {
        return AuthenticationRequest.Builder(getString(R.string.CLIENT_ID), type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(arrayOf(
                        "user-read-recently-played",
                        "user-top-read",
                        "user-library-read",
                        "playlist-modify-private",
                        "playlist-modify-public",
                        "user-read-email",
                        "user-read-currently-playing",
                        "user-read-playback-state",
                        "user-modify-playback-state",
                        "app-remote-control",
                        "streaming",
                        "user-follow-read")).build()
    }

    fun createLoginActivityIntent(request: AuthenticationRequest): Intent {
        val intent = LoginActivity.getAuthIntent(activity, request)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }

    fun onRequestTokenClicked() {
        val request = getAuthenticationRequest(AuthenticationResponse.Type.CODE)
//        AuthenticationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request)
        val intent = createLoginActivityIntent(request)
        startActivityForResult(intent, AUTH_CODE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthenticationClient.getResponse(resultCode, data)

//        if (AUTH_CODE_REQUEST_CODE == requestCode) {
//            mAccessToken = response.accessToken
        mAccessCode = response.code
        Timber.e(mAccessCode)
        val params = listOf("grant_type" to "authorization_code",
                            "code" to mAccessCode,
                            "redirect_uri" to getString(R.string.REDIRECT_URI),
                            "client_id" to getString(R.string.CLIENT_ID),
                            "client_secret" to getString(R.string.CLIENT_SECRET))

        var (_, res, _) = Fuel.post("https://accounts.spotify.com/api/token", params).response()
        var content = JSONObject(res.body().asString("application/json"))
//        var content = JSONObject(NetworkUtils.getRequest("login1",
//                listOf("code" to mAccessCode!!)))
        val token = content.getString("access_token")
        val refresh = content.getString("refresh_token")
        Timber.e("token " + token)
        Timber.e("refresh " + refresh)

//        val json = NetworkUtils.getRequest("https://api.spotify.com/v1/me", null, token, refresh, myDb)
        val json = JSONObject(NetworkUtils.getRequest("login",
                listOf("access_token" to token, "refresh_token" to refresh)))
        val username = json.getString("id")

        val kkk = JSONObject()
        kkk.put("username", username)
//        kkk.put("token", token)
//        kkk.put("refresh", refresh)
        kkk.put("name", json.getString("display_name"))
        val images = json.getJSONArray("images")
        if (images.length()>0)
            kkk.put("img", images.getJSONObject(0).getString("url"))
        else
            kkk.put("img", null)
        kkk.put("email", json.getString("email"))
        kkk.put("owner", true)

        Timber.e(kkk.toString())
        myDb.add(kkk, myDb.writableDatabase, "users")
        myDb.change(username)

        login!!.setVisibility(View.GONE)
        mRecyclerView!!.setVisibility(View.VISIBLE)
        fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
//        }
    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build()
    }
}