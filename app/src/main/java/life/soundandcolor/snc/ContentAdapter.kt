package life.soundandcolor.snc

import android.content.Context
import org.json.JSONException
import org.json.JSONObject

import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.squareup.picasso.Picasso
import life.soundandcolor.snc.utilities.Helper
import timber.log.Timber

class ContentAdapter(id: String, user: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: Array<String?>? = null
    var id1: String
    var user1: String

    init {
        id1 = id
        user1 = user
    }

    inner class ArtistAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mTextView: TextView
        val mTextView2: TextView
        val mTextView3: TextView
        val mTextView4: TextView
        val mImgView: ImageView
        val relLay: RelativeLayout

        init {
            relLay = view.findViewById(R.id.item)
            mImgView = view.findViewById(R.id.item_img)
            mTextView = view.findViewById(R.id.item_data)
            mTextView2 = view.findViewById(R.id.item_data2)
            mTextView3 = view.findViewById(R.id.num)
            mTextView4 = view.findViewById(R.id.item_data3)

            relLay.setOnClickListener {
                Helper.play(view)
            }

            relLay.setOnLongClickListener {child ->
                val context = view.context
                val popup = PopupMenu(context, view)
                popup.inflate(R.menu.item_menu)

                popup.setOnMenuItemClickListener {item: MenuItem ->
                    Timber.e(item.itemId.toString())

                    when (item.itemId) {
                        R.id.Share -> {
                            val uri = child.tag.toString()
                            var shareBody = ""

                            when (id1) {
                                "Following", "Top Artists" -> {
                                    shareBody += "Here's an artist from " + user1 + "'s " + id1 + "... " + mTextView.text
                                }
                                "Recent", "Top Tracks", "Saved Tracks" -> {
                                    shareBody += "Here's a song from " + user1 + "'s " + id1 + "... " +
                                            mTextView.text.split("\t\t/\t\t")[0] + " by " + mTextView2.text
                                }
                                "Saved Albums" -> {
                                    shareBody += "Here's an album from " + user1 + "'s " + id1 + "... " + mTextView2.text
                                }
                            }
                            shareBody += "\n" + uri + "\n\n- via Sound & Color"
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.setType("text/plain")
                                    .putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                                    .putExtra(Intent.EXTRA_TEXT, shareBody)
                            context.startActivity(shareIntent)
                        }

                        R.id.Open -> {
                            Helper.play(view)
                        }

                        R.id.Send -> {
                            child.findNavController().navigate(ContentDirections.actionContentToShare(mData!![position].toString()))
                        }
                    }
                    true
                }
                popup.gravity = Gravity.RIGHT
                popup.show()
                true
            }
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar: ProgressBar

        init {
            progressBar = view.findViewById(R.id.progressBar1)
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     * can use this viewType integer to provide a different layout.
     * @return A new ArtistAdapterViewHolder that holds the View for each list item
     */

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = viewGroup.context
        if (viewType == 0) {
            val layoutIdForListItem = R.layout.item
            val inflater = LayoutInflater.from(context)
            val shouldAttachToParentImmediately = false
            val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
            return ArtistAdapterViewHolder(view)
        }
        else {
            val layoutIdForListItem = R.layout.item_loading
            val inflater = LayoutInflater.from(context)
            val shouldAttachToParentImmediately = false
            val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
            return LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistAdapterViewHolder) {
            try {
                val js = JSONObject(mData!![position])
                val img = js.getString("img")
                if (img.length > 0)
                    Picasso.get().load(img)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .resize(512, 512).into(holder.mImgView)
                else
                    Picasso.get().load(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .resize(512, 512).into(holder.mImgView)

                holder.mTextView3.text = (position+1).toString()
                holder.relLay.tag = js.getString("url")
                val type = js.getString("type")
                if (type == "artist") {
                    holder.mTextView.text = js.getString("name")
                    holder.mTextView2.text = js.getString("pop")

                    //            holder.mTextView.setTag(js.getString("url"));
                    //            holder.mImgView.setTag(js.getString("url"));
                    //            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    //                public boolean onLongClick(View v) {
                    //                    v.setSelected(true);
                    //                    v.setBackgroundResource(R.color.colorPrimaryDark);
                    //                    return true;
                    //                }
                    //            });
                } else if (type == "track") {
                    holder.mTextView.text = js.getString("name") + "  •  " + js.get("album")
                    holder.mTextView2.text = js.getString("artist")

                    if (id1=="Recent") {
                        holder.mTextView.gravity = Gravity.TOP
                        holder.mTextView.setPadding(0,16,0,0)
                        holder.mTextView2.gravity = Gravity.TOP
                        holder.mTextView2.setPadding(0,16,0,0)
                        holder.mTextView4.visibility = View.VISIBLE
                        holder.mTextView4.text = Helper.display_time(js.getString("timestamp"))

                    }
                }

            } catch (e: JSONException) { }

        }
        else if (holder is LoadingViewHolder)
            holder.progressBar.isIndeterminate = true
    }

    override fun getItemCount(): Int {
        return if (null == mData) 0 else mData!!.size
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (mData!!.get(position) == null) 1 else 0
    }

    fun setData(data: Array<String>) {
        mData = data as Array<String?>
        notifyDataSetChanged()
    }

    fun addData(listItems: Array<String>?) {
        Timber.e(listItems!![0].toString())
        mData = mData!!.dropLast(1).toTypedArray()
        notifyItemRemoved(mData!!.size + 1)
        mData= mData!!.plus(listItems)
//        notifyItemRangeChanged(mData!!.size, listItems.size)
        notifyDataSetChanged()
    }

    fun loading() {
        mData = mData!!.plusElement(null)
        notifyItemInserted(mData!!.size)
    }
}