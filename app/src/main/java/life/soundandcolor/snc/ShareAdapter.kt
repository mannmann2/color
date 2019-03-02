package life.soundandcolor.snc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class ShareAdapter(private val context: Context, private val users: ArrayList<String>, val itemClick: (Int) -> Unit)
    : BaseAdapter() {

    lateinit private var simple1: TextView
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.share_item, null, true)

        simple1 = rowView.findViewById(R.id.simple_text) as TextView
        val send = rowView.findViewById(R.id.send) as Button
        val undo = rowView.findViewById(R.id.undo) as Button

        simple1.text = users[position]

        send.setOnClickListener {
            send.visibility = View.GONE
            undo.visibility = View.VISIBLE
            itemClick(position)
        }

        undo.setOnClickListener {
            send.visibility = View.VISIBLE
            undo.visibility = View.GONE
            itemClick(position)
        }
        return rowView
    }
}