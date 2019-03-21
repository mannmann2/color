package life.soundandcolor.snc

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import life.soundandcolor.snc.databinding.StatsTracksBinding
import life.soundandcolor.snc.utilities.DatabaseHelper

class StatsTracks : Fragment() {

    lateinit internal var binding: StatsTracksBinding
    lateinit internal var trending: ArrayList<String>
    lateinit internal var trending2: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            val model = ViewModelProviders.of(it).get(StatsViewModel::class.java)
            trending = model.trending_tracks_day.value!!
            trending2 = model.trending_tracks_week.value!!
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.stats_tracks, container, false)

        val adapter = ArrayAdapter<String>(context, R.layout.simple_no_elevation, trending)
        binding.list.setAdapter(adapter)

        val adapter2 = ArrayAdapter<String>(context, R.layout.simple_no_elevation, trending2)
        binding.list2.setAdapter(adapter2)

        binding.list.setOnTouchListener(object : View.OnTouchListener {
            // Setting on Touch Listener for handling the touch inside ScrollView
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                // Disallow the touch request for parent scroll on touch of child view
                v.parent.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })

        binding.list2.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                v.parent.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })

        (activity as AppCompatActivity).supportActionBar?.title = "Stats / Tracks"
        return binding.root
    }
}