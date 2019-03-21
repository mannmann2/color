package life.soundandcolor.snc

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import life.soundandcolor.snc.databinding.ChatBinding
import life.soundandcolor.snc.utilities.DatabaseHelper
import life.soundandcolor.snc.utilities.NetworkUtils
import org.json.JSONArray
import timber.log.Timber

class Chat : Fragment() {

    lateinit internal var binding: ChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        StrictMode.enableDefaults()
        var args = ChatArgs.fromBundle(arguments)
        val username = args.username
        val name = args.name

        binding = DataBindingUtil.inflate(inflater, R.layout.chat, container, false)

        val myDb = DatabaseHelper(context)
        val res = myDb.get_owner()
        val owner = res.getString(0)

        val listItems = ArrayList<String>()
        val align = ArrayList<Int>()
//        val res2 = myDb.messages(user)
//        while (res2.moveToNext()) {
//            trending.add(res2.getString(0))
//            align.add(res2.getInt(1))
//        }
        val messages = JSONArray(NetworkUtils.getRequest("get-messages", listOf("username" to owner, "friend" to username)))
        Timber.e(messages.toString())
        for (i in 0 until messages.length()) {
            val temp = messages.getJSONObject(i).getJSONObject("_source")
            listItems.add(temp.getString("message"))
            if (temp.getString("from").equals(owner))
                align.add(1)
            else
                align.add(0)
        }

        listItems.reverse()
        align.reverse()
//        val adapter = ArrayAdapter<String>(context, R.layout.simple_no_elevation, trending)
        val adapter = ChatAdapter(context!!, listItems, align)
        binding.list.adapter = adapter

        binding.send.setOnClickListener {
            var message = binding.inputText.text.toString()
            if (message != "") {
                listItems.add(message)
                align.add(1)
                binding.inputText.setText("")
                myDb.send(username, message, 1)
                NetworkUtils.getRequest("send-message", listOf("username" to owner, "friend" to username, "message" to message))

                adapter.notifyDataSetChanged()
                binding.list.smoothScrollToPosition(listItems.size-1)
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = name
        return binding.root

    }
}