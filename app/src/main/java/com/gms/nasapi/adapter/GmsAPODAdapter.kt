package  com.gms.nasapi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gms.nasapi.databinding.ItemLayoutBinding
import com.gms.nasapi.utils.Constants
import com.gms.nasapi.utils.NasaApiResponse
import com.google.gson.Gson
import java.util.*

class GmsAPODAdapter(private val mListener: OnItemClickListener) :
    ListAdapter<NasaApiResponse, GmsAPODAdapter.GmssViewHolder>(ReportsDiffCallback()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GmssViewHolder {
        return GmssViewHolder.reportsViewHolder(this, parent)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: GmssViewHolder, position: Int) {
        val item = getItem(position)//data[position]
        holder.bindReport(item, mListener)
    }

    class GmssViewHolder private constructor(private val itemBinding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        var boolean = true

        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindReport(item: NasaApiResponse, listener: OnItemClickListener) {
            itemBinding.title.text = item.title
            itemBinding.copyright.text = item.copyright
            itemBinding.explanation.text = item.explanation
            itemBinding.date.text = item.date
            Constants.setGilde(itemView.context,item.url,itemBinding.urlImg)

            itemBinding.explanation.setOnClickListener {
                if (boolean) {
                    itemBinding.explanation.maxLines = 20
                } else {
                    itemBinding.explanation.maxLines = 3
                }
                boolean = !boolean

            }

            itemBinding.addToFavorite.setOnClickListener {
               listener.onItemClick(item, adapterPosition)
            }
            itemView.setOnLongClickListener {
               // listener.onItemLongClick(item, adapterPosition)
                true
            }
        }

        companion object {
            fun reportsViewHolder(
                gmsStatusAdapter: GmsAPODAdapter,
                parent: ViewGroup
            ): GmssViewHolder {
                gmsStatusAdapter.context = parent.context
                //val view = LayoutInflater.from(parent.context).inflate(R.layout.report_item, parent, false)
                val binding =
                    ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return GmssViewHolder(binding)
            }

            fun nameImageModelObj(nameImageModel:String): List<NasaApiResponse> {
                val gson = Gson()
                val data: Array<NasaApiResponse> = gson.fromJson(
                    nameImageModel,
                    Array<NasaApiResponse>::class.java
                )
                return data.toList()
            }

        }
    }

    // override fun getItemCount(): Int = data.size

    class ReportsDiffCallback : DiffUtil.ItemCallback<NasaApiResponse>() {
        override fun areItemsTheSame(
            oldItem: NasaApiResponse,
            newItem: NasaApiResponse
        ): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(
            oldItem: NasaApiResponse,
            newItem: NasaApiResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}
