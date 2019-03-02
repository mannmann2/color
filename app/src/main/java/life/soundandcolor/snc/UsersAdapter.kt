package life.soundandcolor.snc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class UsersAdapter(private val context: Context, private val users: ArrayList<String>, private var description: ArrayList<String>?, private val tag: String?)
    : BaseAdapter() {

    lateinit private var simple1: TextView
    lateinit private var simple2: TextView
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
        val rowView = inflater.inflate(R.layout.user_item, null, true)

        simple1 = rowView.findViewById(R.id.simple1) as TextView
        simple2 = rowView.findViewById(R.id.simple2) as TextView

        simple1.text = users[position]
        simple2.text = description!![position]
        if (simple2.text == "")
            simple2.visibility = View.GONE

        return rowView
    }

    fun setData(list1: ArrayList<String>, list2: ArrayList<String>) {
        description = list2
        notifyDataSetChanged()
    }
}