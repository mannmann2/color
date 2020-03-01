package life.soundandcolor.snc

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import life.soundandcolor.snc.databinding.ActivityMainBinding
import timber.log.Timber
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.StrictMode
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()

        Timber.i("onCreate")

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

//        navController.addOnNavigatedListener { nc: NavController, nd: NavDestination ->
//            if (nd.id == nc.graph.startDestination) {
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//            } else {
////                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//            }
//        }
        NavigationUI.setupWithNavController(binding.navView, navController)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }
//            intent?.action == Intent.ACTION_SEND_MULTIPLE
//                    && intent.type?.startsWith("image/") == true -> {
//                handleSendMultipleImages(intent) // Handle multiple images being sent
//            }
            else -> {
                // Handle other intents, such as being started type the home screen
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart")
    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        var fragment: Fragment? = null
//
//        when (item!!.itemId) {
//            R.id.stats -> fragment = Stats()
//
//            R.id.statsArtists -> fragment = StatsArtists()
//
//            R.id.statsTracks -> fragment = StatsTracks()
//        }
//
//        return loadFragment(fragment)
//    }
//
//    private fun loadFragment(fragment: Fragment?): Boolean {
//        if (fragment != null) {
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, fragment!!)
//                    .commit()
//            return true
//        }
//        return false
//    }

    private fun saveImage(bitmap: Bitmap) {

        val root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString()
        val ts = System.currentTimeMillis().toString()
        val file = File(root, "insta/$ts.jpg")

        Timber.e(file.toString())
        val os = FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.flush()
        os.close()
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            val fragment = Share()
            if (it.contains("www.instagram.com")) {
                val (_, response, _) = Fuel.get(it.split("?")[0] +"?__a=1")
                        .response()
                val res = response.body().asString("application/json")
                try {
                    val image = JSONObject(res).getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                    val url = URL(image)
                    Timber.e(url.toString())
                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    saveImage(bitmap)
                } catch(e: IOException) {

                }
            }
            else {
                val arguments = Bundle()
                arguments.putString("share", it)
                fragment.setArguments(arguments)

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.myNavHostFragment, fragment)
                transaction.addToBackStack(null)
            transaction.commit()
            }
        }
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            // Update UI to reflect image being shared
            Timber.e("uri: "+it.toString())
        }
    }

//    private fun handleSendMultipleImages(intent: Intent) {
//        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
//            // Update UI to reflect multiple images being shared
//        }
//    }
}