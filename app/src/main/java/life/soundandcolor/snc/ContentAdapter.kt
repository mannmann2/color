package life.soundandcolor.snc

import android.view.*
import android.widget.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import life.soundandcolor.snc.utilities.Helper
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ContentAdapter(id: String, name: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: Array<String?>? = null
    var id: String
    var name: String

    init {
        this.id = id
        this.name = name
    }

    inner class ArtistAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mNum: TextView
        val mImgView: ImageView
        val mTextView: TextView
        val mTextView2: TextView
        val mTextView3: TextView
        val mTextView4: TextView
        val relLay: RelativeLayout

        init {
            mNum = view.findViewById(R.id.num)
            mImgView = view.findViewById(R.id.item_img)
            mTextView = view.findViewById(R.id.item_data)
            mTextView2 = view.findViewById(R.id.item_data2)
            mTextView3 = view.findViewById(R.id.item_data3)
            mTextView4 = view.findViewById(R.id.item_data4)
            relLay = view.findViewById(R.id.item)

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

                            when (id) {
                                "Following", "Top Artists" -> {
                                    shareBody += "Here's an artist from " + name + "'s " + id + "... " + mTextView.text
                                }
                                "Recent", "Top Tracks", "Saved Tracks" -> {
                                    shareBody += "Here's a song from " + name + "'s " + id + "... " +
                                            mTextView.text.split("  •  ")[0] + " by " + mTextView2.text
                                }
                                "Saved Albums", "New Releases", "For You" -> {
                                    shareBody += "Here's an album from " + name + "'s " + id + "... " + mTextView2.text
                                }
                            }
                            Helper.share(context, shareBody, uri)
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

                holder.mNum.text = (position+1).toString()
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

                    if (id=="Recent") {
                        holder.mTextView.gravity = Gravity.TOP
                        holder.mTextView.setPadding(0,24,0,0)
                        holder.mTextView2.gravity = Gravity.TOP
                        holder.mTextView2.setPadding(0,24,0,0)
                        holder.mTextView3.visibility = View.VISIBLE
                        holder.mTextView3.text = Helper.displayTime(js.getString("timestamp"))
                    }
                }
                else if (type == "album") {
                    holder.mTextView.text = js.getString("name")
                    holder.mTextView2.text = js.getString("artist")
                    holder.mTextView.gravity = Gravity.TOP
                    holder.mTextView.setPadding(0,36,0,0)
                    holder.mTextView2.gravity = Gravity.TOP
                    holder.mTextView2.setPadding(0,36,0,0)
                    holder.mTextView3.visibility = View.VISIBLE
                    holder.mTextView4.visibility = View.VISIBLE
                    holder.mTextView3.text = js.getString("text3")
                    holder.mTextView4.text = js.getString("text4")
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
//        notifyItemRangeChanged(mData!!.size, trending.size)
        notifyDataSetChanged()
    }

    fun loading() {
        mData = mData!!.plusElement(null)
        notifyItemInserted(mData!!.size)
    }
}