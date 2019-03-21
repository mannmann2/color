package life.soundandcolor.snc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import life.soundandcolor.snc.databinding.StatsBinding
import org.json.JSONArray
import java.text.SimpleDateFormat

class Stats : Fragment() {

    lateinit internal var binding: StatsBinding
    //    lateinit internal var d1: Date
//    lateinit internal var d3: Date
    lateinit internal var timeline: JSONArray
    lateinit internal var artists: ArrayList<String>
    lateinit internal var tracks: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use observe() to get live data
        activity?.let {
            val model = ViewModelProviders.of(it).get(StatsViewModel::class.java)
            timeline = model.timeline.value!!
            artists = model.artists.value!!
            tracks = model.tracks.value!!
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.stats, container, false)

        val len = timeline.length()
        var formatter = SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'")
        val series = BarGraphSeries<DataPoint>()
        for (i in 3..len-1) {
            val temp = timeline.getJSONObject(i)
//            if (i==0)
//                d1 = formatter.parse(temp.getString("key_as_string"))
//            else if (i==len-5)
//                d3 = formatter.parse(temp.getString("key_as_string"))
            series.appendData(DataPoint(formatter.parse(temp.getString("key_as_string")), temp.getInt("doc_count").toDouble()), true, len-3)
        }
        val graph = binding.graph
        graph.addSeries(series)
        graph.getGridLabelRenderer().setLabelFormatter(DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(len-3)
//        graph.getViewport().setMinX(d1.time.toDouble())
//        graph.getViewport().setMaxX(d3.time.toDouble())
        graph.getViewport().setXAxisBoundsManual(true)
        graph.getGridLabelRenderer().setHumanRounding(false)

        val adapter = ArrayAdapter<String>(context, R.layout.simple_no_elevation, artists)
        binding.list.setAdapter(adapter)

        val adapter2 = ArrayAdapter<String>(context, R.layout.simple_no_elevation, tracks)
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

        (activity as AppCompatActivity).supportActionBar?.title = "Stats"
        return binding.root
    }
}