package life.soundandcolor.snc


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import life.soundandcolor.snc.utilities.Helper
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class FeedAdapter(val itemClick: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: ArrayList<JSONObject?>? = null
    lateinit private var context: Context
    lateinit private var vg: ViewGroup

    inner class ArtistAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val relLay = view.findViewById(R.id.feed_item) as RelativeLayout
        val mImgView: ImageView
        val mTextView: TextView
        val mTextView2: TextView
        val mTextView3: TextView
        val mTextView4: TextView
        val mTextView5: TextView
        val like: ImageView
        val analysis: ImageView
        val share: ImageView
        val queue1: ImageView
        val queue: ImageView
        val play: ImageView
        val send: ImageView

        init {
            mImgView = view.findViewById(R.id.feed_item_img)
            mTextView = view.findViewById(R.id.feed_user)
            mTextView2 = view.findViewById(R.id.feed_item_data)
            mTextView3 = view.findViewById(R.id.feed_item_data2)
            mTextView4 = view.findViewById(R.id.feed_item_data3)
            mTextView5 = view.findViewById(R.id.feed_item_data4)
            play = view.findViewById(R.id.feed_play)
            analysis = view.findViewById(R.id.feed_analysis)
            queue = view.findViewById(R.id.feed_queue)
            queue1 = view.findViewById(R.id.feed_queue1)
            share = view.findViewById(R.id.feed_share)
            like = view.findViewById(R.id.feed_like)
            send = view.findViewById(R.id.feed_send)

            send.setOnClickListener( {itemClick(layoutPosition)} )

            play.setOnClickListener {
                Helper.play(view)
            }

            analysis.setOnClickListener { v: View ->
                v.findNavController().navigate(HomeDirections.actionHomeToAnalysis(analysis.tag as String, "feed"))
            }

            share.setOnClickListener{
                var shareBody = "Here's a song played by " + mTextView.text + "... " +
                        mTextView2.text + " by " + mTextView3.text
                Helper.share(view.context, shareBody, relLay.tag.toString())
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
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar: ProgressBar

        init {
            progressBar = view.findViewById(R.id.progressBar1)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            vg = viewGroup
            context = vg.context
            val layoutIdForListItem = R.layout.feed_item
            val inflater = LayoutInflater.from(context)
            val shouldAttachToParentImmediately = false
            val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
            return ArtistAdapterViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistAdapterViewHolder) {
            try {
                val js = mData!![position]
                val img = js!!.getString("img")
                holder.mImgView.tag = img
                if (img.length > 0)
                    Picasso.get().load(img)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .resize(512, 512).into(holder.mImgView)
                else
                    Picasso.get().load(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .resize(512, 512).into(holder.mImgView)

                holder.mTextView.text =  js.getString("display_name")
                holder.mTextView2.text = js.getString("name")
                holder.mTextView3.text = js.getString("album")
                holder.mTextView4.text = js.getString("artist")
                holder.mTextView5.text = Helper.displayTime(js.getString("timestamp"))

                holder.relLay.tag = js.getString("url")
                holder.analysis.tag = js.getString("id")

            } catch (e: JSONException) { }
        }
        else if (holder is LoadingViewHolder)
            holder.progressBar.isIndeterminate = true
    }

    override fun getItemCount(): Int {
        return if (null == mData) 0 else mData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData!!.get(position) == null) 1 else 0
    }

    fun setData(data: ArrayList<JSONObject>) {
        mData = data as ArrayList<JSONObject?>
        notifyDataSetChanged()
    }
}