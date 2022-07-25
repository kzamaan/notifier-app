package com.softxilla.notification_forwarder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.softxilla.notification_forwarder.R
import com.softxilla.notification_forwarder.data.model.LocalMessage
import java.util.Locale
import kotlin.collections.ArrayList

class MessageAdapter(
    private val messages: ArrayList<LocalMessage>,
    private val mContext: Context,
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>(), Filterable {

    var filterList = ArrayList<LocalMessage>()

    init {
        filterList = messages
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(newMessages: List<LocalMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val androidTitle: TextView = view.findViewById(R.id.tv_message_title)
        val androidText: TextView = view.findViewById(R.id.tv_message_body)
        val messageDate: TextView = view.findViewById(R.id.tv_message_date)
        val messageStatus: TextView = view.findViewById(R.id.tv_message_status)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.model_message_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = filterList[position]
        holder.androidTitle.text = model.androidTitle
        holder.androidText.text = model.androidText
        holder.messageDate.text = model.createdAt
        if (model.status == 1) {
            holder.messageStatus.text = "Success"
            holder.messageStatus.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.success_color
                )
            )
            holder.messageStatus.compoundDrawableTintList = ContextCompat.getColorStateList(
                mContext,
                R.color.success_color
            )
        } else {
            holder.messageStatus.text = "Pending"
            holder.messageStatus.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.error_color
                )
            )
            holder.messageStatus.compoundDrawableTintList = ContextCompat.getColorStateList(
                mContext,
                R.color.error_color
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
                    messages
                } else {
                    val resultList = ArrayList<LocalMessage>()
                    for (model in filterList) {
                        if (model.androidText.lowercase(Locale.ROOT).contains(searchKey)) {
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
                filterList = results?.values as ArrayList<LocalMessage>
                notifyDataSetChanged()
            }
        }
    }
}