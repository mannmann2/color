package life.soundandcolor.snc

import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.UsersBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import life.soundandcolor.snc.utilities.NetworkUtils.getFriends
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class Users : Fragment() {

    lateinit internal var res: Cursor
    lateinit internal var js: JSONArray
    lateinit internal var myDb: DatabaseHelper
    lateinit internal var binding: UsersBinding
    lateinit internal var adapter: UsersAdapter
    lateinit internal var owner: String
    lateinit internal var names: ArrayList<String>
    lateinit internal var usernames: ArrayList<String>
    lateinit internal var listItems: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDb = DatabaseHelper(context)
        res = myDb.get_owner()

        owner = res.getString(0)
        val owner_name = res.getString(1)

        names = ArrayList<String>()
        usernames = ArrayList<String>()
        listItems = ArrayList<String>()
        names.add(owner_name)
        usernames.add(owner)
        listItems.add("")

        js = getFriends(context!!, owner)
        Timber.e(js.toString())
        for (i in 0 until js.length()) {

            val temp = js.getJSONObject(i)

            val kkk = JSONObject()
            kkk.put("username", temp.getString("id"))
            kkk.put("name", temp.getString("display_name"))
            val images = temp.getJSONArray("images")
            if (images!=null && images.length() > 0)
                kkk.put("img", images.getJSONObject(0).getString("url"))
            kkk.put("email", temp.getString("email"))
            kkk.put("token", temp.getString(("access_token")))
            kkk.put("refresh", temp.getString(("refresh_token")))
            myDb.add(kkk, myDb.writableDatabase, "users")

            names.add(temp.getString("display_name"))
            usernames.add(temp.getString("id"))
            listItems.add("")
        }
//        while (res.moveToNext()) {
//            names.add(res.getString(0))
//            trending.add("")
//        }
//        Fetch().execute()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        StrictMode.enableDefaults()
        binding = DataBindingUtil.inflate(
                inflater, R.layout.users, container, false)

        binding.add.setOnClickListener {

            binding.input.visibility = View.VISIBLE
            binding.add.visibility = View.GONE
//            binding.response.visibility = View.GONE
        }

        binding.send.setOnClickListener {
//            binding.response.text = "Request sent."
//            binding.response.visibility = View.VISIBLE
            val friend = binding.inputText.text.toString()
            if (NetworkUtils.getRequest("user", listOf("username" to friend)).equals("1")) {
                NetworkUtils.getRequest("add-friend", listOf("username" to owner, "friend" to friend))
                binding.inputText.setText("")
                binding.input.visibility = View.GONE
                Toast.makeText(context, "Request sent", Toast.LENGTH_LONG).show()
                binding.add.visibility = View.VISIBLE
            } else
                Toast.makeText(context, "Invalid username", Toast.LENGTH_SHORT).show()
        }

        adapter = UsersAdapter(context!!, names, listItems, tag)
        binding.list.setAdapter(adapter)

        binding.list.setOnItemClickListener { parent, view, position, id ->
            val name = names[position]
            val username = usernames[position]
            Toast.makeText(context, "$name selected", Toast.LENGTH_SHORT).show()
            
            myDb.change(username)
            parent.findNavController().navigate(UsersDirections.actionUsersToHome())
        }
        return binding.root
    }

    inner class Fetch : AsyncTask<String, Void, Array<String>>() {

        var listItems2 = ArrayList<String>()

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): Array<String>? {
//            res = myDb.get("users")
//            while (res.moveToNext()) {
//                Timber.e(res.getString(0))
//                names.add(res.getString(0))

//                var result = NetworkUtils.getRequest("https://api.spotify.com/v1/me/player/currently-playing",
//                        null, res.getString(1), res.getString(2), myDb)
//                if (result != null) {
//                    var json = result.getJSONObject("item")
//
//                    trending.add(json.getString("name") + "  •  " +
//                            json.getJSONArray("artists").getJSONObject(0).getString("name") + "  •  " +
//                            json.getJSONObject("album").getString("name"))
//                } else
//                    trending.add("")
//            }
            for (i in usernames) {
                val result = NetworkUtils.getRequest("currently-playing",
                        listOf("username" to i))
                if (!result.equals("ok")) {
                    Timber.e(i+result.toString())

                    var json = JSONObject(result).getJSONObject("item")
                    listItems2.add(json.getString("name") + "  •  " +
                            json.getJSONArray("artists").getJSONObject(0).getString("name") + "  •  " +
                            json.getJSONObject("album").getString("name"))
                }  else
                    listItems2.add("")
            }
            return null
        }

        override fun onPostExecute(data: Array<String>?) {
//            adapter = UsersAdapter(context!!, names, trending)
//            adapter.notifyDataSetChanged()
            adapter.setData(names, listItems2)
        }
    }
}

