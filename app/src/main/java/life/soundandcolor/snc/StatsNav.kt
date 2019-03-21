package life.soundandcolor.snc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.stats_nav.*
import life.soundandcolor.snc.databinding.StatsBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import org.json.JSONArray
import java.util.ArrayList

class StatsNav : Fragment() {

    lateinit internal var binding: StatsBinding
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var owner: String
    lateinit internal var trending: ArrayList<String>
    lateinit internal var trending2: ArrayList<String>
    lateinit internal var timeline: JSONArray
    private lateinit var model: StatsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myDb = DatabaseHelper(context)
        owner = myDb.get_owner().getString(0)

        activity?.run {
            model = ViewModelProviders.of(this).get(StatsViewModel::class.java)
            model.select(owner)

        } ?: throw Exception("Invalid Activity")


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.stats_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireActivity(), R.id.bottomNavFragment)

//        navController.addOnNavigatedListener { controller, destination ->
//            when (destination.id) {
//                R.id.stats -> {}
//                R.id.statsTracks -> {Timber.e("Helooe3")}
//                R.id.statsArtists -> {}
//            }
//        }
        bottomNavigation.setupWithNavController(navController)
    }
}