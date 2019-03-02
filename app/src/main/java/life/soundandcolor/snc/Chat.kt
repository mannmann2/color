package life.soundandcolor.snc

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import life.soundandcolor.snc.databinding.ChatBinding
import life.soundandcolor.snc.utilities.DatabaseHelper

class Chat : Fragment() {

    lateinit internal var binding: ChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        StrictMode.enableDefaults()
        var args = ChatArgs.fromBundle(arguments)
        val user = args.user

        binding = DataBindingUtil.inflate(inflater, R.layout.chat, container, false)

        val myDb = DatabaseHelper(context)
        val res = myDb.messages(user)

        val listItems = ArrayList<String>()
        val align = ArrayList<Int>()
        while (res.moveToNext()) {
            listItems.add(res.getString(0))
            align.add(res.getInt(1))
        }

//        val adapter = ArrayAdapter<String>(context, R.layout.simple_no_elevation, listItems)
        val adapter = ChatAdapter(context!!, listItems, align)
        binding.list.adapter = adapter

        binding.send.setOnClickListener {
            var message = binding.inputText.text.toString()
            if (message != "") {
                listItems.add(message)
                align.add(1)
                binding.inputText.setText("")
                myDb.send(user, message, 1)
                adapter.notifyDataSetChanged()
                binding.list.smoothScrollToPosition(listItems.size-1)
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = user
        return binding.root

    }
}