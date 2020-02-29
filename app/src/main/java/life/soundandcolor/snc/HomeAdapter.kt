package life.soundandcolor.snc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import life.soundandcolor.snc.utilities.Helper
import org.json.JSONObject

class HomeAdapter(private val context: Context, private var mData: ArrayList<JSONObject>, val itemClick: (Int) -> Unit)
    : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return if (null == mData) 0 else mData!!.size
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, holder: View?, parent: ViewGroup): View {
        val holder = inflater.inflate(R.layout.feed_item, parent, false)

        val relLay = holder.findViewById(R.id.feed_item) as RelativeLayout
        val mImgView = holder.findViewById(R.id.feed_item_img) as ImageView
        val mTextView = holder.findViewById(R.id.feed_user) as TextView
        val mTextView2 = holder.findViewById(R.id.feed_item_data) as TextView
        val mTextView3 = holder.findViewById(R.id.feed_item_data2) as TextView
        val mTextView4 = holder.findViewById(R.id.feed_item_data3) as TextView
        val mTextView5 = holder.findViewById(R.id.feed_item_data4) as TextView
        val play = holder.findViewById(R.id.feed_play) as ImageView
        val analysis = holder.findViewById(R.id.feed_analysis) as ImageView
        val share = holder.findViewById(R.id.feed_share) as ImageView
        val like = holder.findViewById(R.id.feed_like) as ImageView
        val send = holder.findViewById(R.id.feed_send) as ImageView

        send.setOnClickListener( {itemClick(position)} )

        play.setOnClickListener {
            Helper.play(holder)
        }

        analysis.setOnClickListener { v: View ->
            v.findNavController().navigate(HomeDirections.actionHomeToAnalysis(analysis.tag as String, "feed"))
        }

        share.setOnClickListener{
            var shareBody = "Here's a song played by " + mTextView.text + "... " +
                    mTextView2.text + " by " + mTextView3.text
            Helper.share(holder.context, shareBody, relLay.tag.toString())
        }

        like.setOnClickListener {
            if (like.tag == 1) {
                like.setTag(0)
                like.setImageResource(R.drawable.ic_like_border_black)
            }
            else {
                like.setTag(1)
                like.setImageResource(R.drawable.ic_like_red)
            }
        }

        val js = mData!![position]
        val img = js!!.getString("img")
        mImgView.tag = img
        if (img.length > 0)
            Picasso.get().load(img)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .resize(512, 512).into(mImgView)
        else
            Picasso.get().load(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .resize(512, 512).into(mImgView)

        mTextView.text =  js.getString("display_name")
        mTextView2.text = js.getString("name")
        mTextView3.text = js.getString("album")
        mTextView4.text = js.getString("artist")
        mTextView5.text = Helper.displayTime(js.getString("timestamp"))

        relLay.tag = js.getString("url")
        analysis.tag = js.getString("id")

        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData!!.get(position) == null) 1 else 0
    }

    fun setData(data: ArrayList<JSONObject>) {
        mData = data
        notifyDataSetChanged()
    }
}