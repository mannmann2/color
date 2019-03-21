package life.soundandcolor.snc

import android.database.Cursor
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.ProfileBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import org.json.JSONArray
import java.util.*


class Profile : Fragment() {

    lateinit internal var myDb: DatabaseHelper
    lateinit internal var res: Cursor
    lateinit internal var owner: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: ProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.profile, container, false)

        // reinitialized bcuz on going back you still want the last selected username to be shown and not an older username
        myDb = DatabaseHelper(context)
        res = myDb.get_current()
        owner = myDb.get_owner().getString(0)

        binding.userId.setText(res.getString(1))

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


//        binding.gg.setOnClickListener { v: View ->
//            with(NotificationManagerCompat.type(context!!)) {
//                val nn = RealNotifications(context!!)
//                notify(8, nn.buildNotification2())
//            }
//        }
//
//        binding.gp.setOnClickListener { v: View ->
////            Intent(context, MessageService::class.java).also { intent ->
////                activity!!.startService(intent)
////            }
////
//            val serviceIntent = Intent(activity, MessageService::class.java).apply {
//                putExtra("download_url", "http://13.234.48.148:5000")
//            }
//
////            val RSS_JOB_ID = 1000
////            MessageService.enqueueWork(context, MessageService::class.java, RSS_JOB_ID, serviceIntent)
//            Timber.e("hellpo2")
//            activity!!.startService(serviceIntent)
//            Timber.e("hellpo")
//        }
        if (res.getString(0).equals(owner)) {

            binding.stats.setOnClickListener { v: View ->
                v.findNavController().navigate(ProfileDirections.actionProfileToStatsNav())
            }
            binding.stats.visibility = View.VISIBLE
        }
//        (activity as AppCompatActivity).supportActionBar?.title = "Sound & Color"
        (activity as AppCompatActivity).supportActionBar!!.isHideOnContentScrollEnabled = false
        setHasOptionsMenu(true)
        return binding.root
    }

//    override fun onPause() {
//        super.onPause()
//        Timber.e("hellow")
//    }
//    override fun onStop() {
//        super.onStop()
//        Timber.e("hellow22")
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        Timber.e("hellow")
//    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        Timber.e("okay")
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putStringArrayList("list1", trending)
//        outState.putStringArrayList("list2", trending2)
//        Timber.e("onSaveInstanceState Called")
//        super.onSaveInstanceState(outState)
//    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        super.onCreateOptionsMenu(menu, inflater)
//        val img = res2.getString(2)
////        if (img!=null)
////            menu!!.getItem(0).actionView.mImgView.setImageURI(img as Uri)
////            Picasso.get().load(img)
////                    .placeholder(R.drawable.ic_launcher_background)
////                    .error(R.drawable.ic_launcher_background)
////                    .resize(512, 512).into(menu!!.getItem(0).mImgView)
////
////        else
////            Picasso.get().load(R.drawable.ic_launcher_background)
////                    .error(R.drawable.ic_launcher_background)
////                    .resize(512, 512).into(menu!!.getItem(0).actionView.mImgView)
//        inflater?.inflate(R.menu.profile_menu, menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        myDb.change(res2.getString(0))
//        return true
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
//                    setResponse("Failed to parseMessage data: $e")
//                }
//
//            }
//        })
//    }
}
