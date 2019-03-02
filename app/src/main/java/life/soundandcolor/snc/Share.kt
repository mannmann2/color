package life.soundandcolor.snc

import android.database.Cursor
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import life.soundandcolor.snc.databinding.ShareBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.Helper
import java.util.ArrayList

class Share: Fragment() {

    lateinit internal var res: Cursor
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var binding: ShareBinding
    lateinit internal var adapter: ShareAdapter
    private var selected_count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        StrictMode.enableDefaults()
        var args = ShareArgs.fromBundle(arguments)
        val share = args.share

        binding = DataBindingUtil.inflate(
                inflater, R.layout.share, container, false)

        myDb = DatabaseHelper(context)

        var listItems = ArrayList<String>()
        var listItems2 = ArrayList<Int>()
        res = Helper.getFriends(myDb)
        while (res.moveToNext())
            listItems.add(res.getString(0))

        adapter = ShareAdapter(context!!, listItems){
            var x = selected_count

            if (!(it in listItems2)) {
                listItems2.add(it)
                selected_count += 1
            }
            else {
                selected_count -= 1
                listItems2.remove(it)
            }
            if (selected_count == 0)
                binding.done.visibility = View.GONE
            else if (selected_count == 1 && x == 0)
                binding.done.visibility = View.VISIBLE
        }
        binding.list.setAdapter(adapter)

        binding.done.setOnClickListener {
            for (position in listItems2) {
                myDb.send(listItems[position], share, 1)
            }
            Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show()
            fragmentManager!!.popBackStack()
        }

        (activity as AppCompatActivity).supportActionBar!!.title = "Share..."
        return binding.root
    }

}