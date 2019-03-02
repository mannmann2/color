package life.soundandcolor.snc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import life.soundandcolor.snc.databinding.ChatsBinding
import life.soundandcolor.snc.utilities.DatabaseHelper

class Chats : Fragment() {

    lateinit internal var binding: ChatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.chats, container, false)

        val myDb = DatabaseHelper(context)
        val res = myDb.chats()

        val listItems = ArrayList<String>()
        while (res.moveToNext()) {
            listItems.add(res.getString(0))
        }

        val adapter = ArrayAdapter<String>(context, R.layout.simple, listItems)

        binding.list.adapter = adapter
        binding.list.setOnItemClickListener { parent, view, position, id ->
            val user = listItems[position]

            parent.findNavController().navigate(ChatsDirections.actionChatsToChat(user))
        }

        return binding.root

    }
}