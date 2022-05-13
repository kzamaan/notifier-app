package me.kzaman.notification_forwarder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import me.kzaman.notification_forwarder.R
import me.kzaman.notification_forwarder.data.model.ApplicationModel
import me.kzaman.notification_forwarder.utils.goToNextFragment
import java.util.Locale
import kotlin.collections.ArrayList

class ApplicationAdapter(
    private val applicationModels: ArrayList<ApplicationModel>,
    private val mContext: Context,
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>(), Filterable {

    var filterList = ArrayList<ApplicationModel>()

    init {
        filterList = applicationModels
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_customer_name)
        val location: TextView = view.findViewById(R.id.tv_location)
        val appIcon: CircleImageView = view.findViewById(R.id.iv_app_icon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.model_app_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = filterList[position]

        holder.name.text = model.appName
        holder.location.text = model.packageName
        holder.appIcon.setImageDrawable(model.appIcon)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("packageName", model.packageName)
            goToNextFragment(
                R.id.action_applicationListFragment_to_applicationDetailFragment,
                holder.itemView,
                bundle
            )
        }
    }

    override fun getItemCount() = filterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchKey = constraint.toString().lowercase(Locale.ROOT)
                Log.d("charSearch", searchKey)
                filterList = if (searchKey.isEmpty()) {
                    applicationModels
                } else {
                    val resultList = ArrayList<ApplicationModel>()
                    for (model in filterList) {
                        if (model.appName.lowercase(Locale.ROOT).contains(searchKey)) {
                            resultList.add(model)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<ApplicationModel>
                notifyDataSetChanged()
            }
        }
    }
}