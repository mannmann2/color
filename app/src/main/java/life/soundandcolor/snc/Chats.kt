package life.soundandcolor.snc

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.ChatsBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils.getFriendObjects


class Chats : Fragment() {

    lateinit internal var binding: ChatsBinding
    lateinit internal var usernames: ArrayList<String>
    lateinit internal var names: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myDb = DatabaseHelper(context)
        val res = myDb.check()
        res.moveToFirst()

        val js = getFriendObjects(res.getString(0))
        usernames = ArrayList<String>()
        names = ArrayList<String>()
        for (i in 0 until js.length()) {
            val temp = js.getJSONObject(i)
            usernames.add(temp.getString("id"))
            names.add(temp.getString("display_name"))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        StrictMode.enableDefaults()

        binding = DataBindingUtil.inflate(inflater, R.layout.chats, container, false)

        val adapter = ArrayAdapter<String>(context, R.layout.simple, names)

        binding.list.adapter = adapter
        binding.list.setOnItemClickListener { parent, view, position, id ->
            parent.findNavController().navigate(ChatsDirections.actionChatsToChat(usernames[position], names[position]))
        }
        return binding.root
    }
}