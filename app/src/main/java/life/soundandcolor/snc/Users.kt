package life.soundandcolor.snc

import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.user_item.view.*
import life.soundandcolor.snc.databinding.UsersBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.Helper
import life.soundandcolor.snc.utilities.NetworkUtils
import timber.log.Timber

import java.util.ArrayList

class Users : Fragment() {

    lateinit internal var res: Cursor
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var binding: UsersBinding
    lateinit internal var adapter: UsersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        StrictMode.enableDefaults()
        binding = DataBindingUtil.inflate(
                inflater, R.layout.users, container, false)

        myDb = DatabaseHelper(context)

        var listItems1 = ArrayList<String>()
        var listItems2 = ArrayList<String>()
        res = Helper.getFriends(myDb)
        while (res.moveToNext()) {
            listItems1.add(res.getString(0))
            listItems2.add("")
        }

        Fetch().execute()
        binding.add.setOnClickListener {

            binding.input.visibility = View.VISIBLE
            binding.add.visibility = View.GONE
//            binding.response.visibility = View.GONE
        }

        binding.send.setOnClickListener {
//            binding.response.text = "Request sent."
//            binding.response.visibility = View.VISIBLE
            if (true) {//valid username:
                binding.inputText.setText("")
                binding.input.visibility = View.GONE
                Toast.makeText(context, "Request sent", Toast.LENGTH_LONG).show()
                binding.add.visibility = View.VISIBLE
            } else
                Toast.makeText(context, "Invalid username", Toast.LENGTH_SHORT).show()
        }

        adapter = UsersAdapter(context!!, listItems1, listItems2, tag)
        binding.list.setAdapter(adapter)

        binding.list.setOnItemClickListener { parent, view, position, id ->
            val user = listItems1[position]
            Toast.makeText(context, "$user selected", Toast.LENGTH_SHORT).show()
            
            myDb.change(user)
            parent.findNavController().navigate(UsersDirections.actionUsersToHome())
        }
        return binding.root
    }

    inner class Fetch : AsyncTask<String, Void, Array<String>>() {

        var url = "https://api.spotify.com/v1/me/player/currently-playing"

        var listItems1 = ArrayList<String>()
        var listItems2 = ArrayList<String>()

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): Array<String>? {
            res = myDb.get2("users")
            while (res.moveToNext()) {
                Timber.e(res.getString(0))
                var result = NetworkUtils.getRequest(url, null, res.getString(1), res.getString(2), myDb)
                if (result != null) {
                    var json = result.getJSONObject("item")

                    listItems1.add(res.getString(0))
                    listItems2.add(json.getString("name") + "  •  " +
                            json.getJSONArray("artists").getJSONObject(0).getString("name") + "  •  " +
                            json.getJSONObject("album").getString("name"))
                } else {
                    listItems1.add(res.getString(0))
                    listItems2.add("")
                }
            }
            return null
        }

        override fun onPostExecute(data: Array<String>?) {
//            adapter = UsersAdapter(context!!, listItems1, listItems2)
//            adapter.notifyDataSetChanged()
            adapter.setData(listItems1, listItems2)

        }
    }
}

