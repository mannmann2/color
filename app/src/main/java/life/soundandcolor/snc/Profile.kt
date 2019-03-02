package life.soundandcolor.snc

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.ProfileBinding
import life.soundandcolor.snc.utilities.DatabaseHelper

class Profile : Fragment() {

    lateinit internal var myDb: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        StrictMode.enableDefaults()
        val binding: ProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.profile, container, false)

        myDb = DatabaseHelper(context)
        val res = myDb.get()
        res.moveToFirst()

        binding.userId.setText(res.getString(0))

        binding.fab.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToHome())
        }
        binding.userId.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToUsers())
        }

        binding.following.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Following", v.getTag().toString()))
        }
        binding.top11.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Artists", v.getTag().toString()))
        }
        binding.top12.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Artists", v.getTag().toString()))
        }
        binding.top13.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Artists", v.getTag().toString()))
        }

        binding.recent.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Recent", v.getTag().toString()))
        }

        binding.top21.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Tracks", v.getTag().toString()))
        }
        binding.top22.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Tracks", v.getTag().toString()))
        }
        binding.top23.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Top Tracks", v.getTag().toString()))
        }
        binding.saved1.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Saved Tracks", v.getTag().toString()))
        }

        binding.saved2.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToContent("Saved Albums", v.getTag().toString()))
        }

        binding.genres.setOnClickListener { v: View ->
            v.findNavController().navigate(ProfileDirections.actionProfileToGenres())
        }

        binding.gg.setOnClickListener { v: View ->
            with(NotificationManagerCompat.from(context!!)) {
                val nn = RealNotifications(context!!)
                notify(8, nn.buildNotification())
            }
        }

//        (activity as AppCompatActivity).supportActionBar?.title = "Sound & Color"
        (activity as AppCompatActivity).supportActionBar!!.isHideOnContentScrollEnabled = false
//        setHasOptionsMenu(true)
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater?.inflate(R.menu.overflow_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
////        if (item!!.itemId == R.id.login) {
////                onRequestTokenClicked(view!!)
////        }
////        else {
//            return NavigationUI.onNavDestinationSelected(item!!,
//                        view!!.findNavController()) || super.onOptionsItemSelected(item)
////        }
////        return true
//    }


//    fun onGetUserProfileClicked(view: View) {
//        if (mAccessToken == null) {
//            val snackbar = Snackbar.make(findViewById(R.id.profile), R.string.warning_need_token, Snackbar.LENGTH_SHORT)
//            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
//            snackbar.show()
//            return
//        }
//
//        val request = Request.Builder()
//                .url("https://api.spotify.com/v1/me")
//                .addHeader("Authorization", "Bearer $mAccessToken")
//                .build()
//
//        cancelCall()
//        mCall = mOkHttpClient.newCall(request)
//
//        mCall.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                setResponse("Failed to fetch data: $e")
//            }
//
//            @Throws(IOException::class)
//            override fun onResponse(call: Call, response: Response) {
//                try {
//                    val jsonObject = JSONObject(response.body()!!.string())
//                    setResponse(jsonObject.toString(3))
//                } catch (e: JSONException) {
//                    setResponse("Failed to parse_message data: $e")
//                }
//
//            }
//        })
//    }
}
