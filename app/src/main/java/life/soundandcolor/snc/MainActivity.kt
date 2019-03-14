package life.soundandcolor.snc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import life.soundandcolor.snc.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        return NavigationUI.navigateUp(drawerLayout, navController)
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

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            val fragment = Share()
            val arguments = Bundle()
            arguments.putString("share", it)
            fragment.setArguments(arguments)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.myNavHostFragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
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