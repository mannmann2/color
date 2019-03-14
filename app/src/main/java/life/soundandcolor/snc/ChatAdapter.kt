package life.soundandcolor.snc

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import life.soundandcolor.snc.utilities.Helper
import org.json.JSONObject

class ChatAdapter(private val context: Context, private val messages: ArrayList<String>, private val align: ArrayList<Int>)
    : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(position: Int): Any {
        return messages[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.chat_item, null, true)

        val lay = view.findViewById(R.id.lay) as RelativeLayout
        val simple1 = view.findViewById(R.id.simple1) as TextView
        val relLay = view.findViewById(R.id.item) as RelativeLayout

        relLay.setOnClickListener {
            Helper.play(relLay)
        }

        val message = messages[position]
        if ("url\":\"http" in message) {
            simple1.visibility = View.GONE

            val mImgView = view.findViewById(R.id.item_img) as ImageView
            val mTextView = view.findViewById(R.id.item_data) as TextView
            val mTextView2 = view.findViewById(R.id.item_data2) as TextView

            val js = JSONObject(message)

            relLay.tag = js.getString("url")
            val img = js.getString("img")
            if (img.length > 0)
                Picasso.get().load(img)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .resize(512, 512).into(mImgView)
            else
                Picasso.get().load(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .resize(512, 512).into(mImgView)

            val type = js.getString("type")
            if (type == "artist") {
                mTextView.text = js.getString("name")
                mTextView2.text = js.getString("pop")
            } else if (type == "track") {
                mTextView.text = js.getString("name") + "  •  " + js.get("album")
                mTextView2.text = js.getString("artist")
            }
        }
        else if ("open.spotify.com" in message) {
            simple1.visibility = View.GONE

            val mTextView = view.findViewById(R.id.item_data) as TextView
            mTextView.text = message
            relLay.tag = message

        }
        else {
            simple1.text = message
            relLay.visibility = View.GONE
        }

        if (align[position] == 1) {
            lay.gravity = Gravity.END
        }
        else {
            lay.gravity = Gravity.START
        }

        return view
    }
}