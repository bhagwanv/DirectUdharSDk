package com.sk.directudhar.ui.myaccount.udharStatement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sk.directudhar.MyApplication
import com.sk.directudhar.R
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.utils.Utils

class HistoryAdapter(private val items: ArrayList<HistoryResponseModel>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val txnId: TextView = itemView.findViewById(R.id.tvTxnId)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val amount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_items_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.txnId.text = item.TrasanctionId
        holder.status.text = item.Status
        holder.date.text = Utils.simpleDateFormate(item.CreatedDate!!,"yyyy-MM-dd'T'HH:mm:ss.SSS", "dd MMMM yyyy")
    }
    override fun getItemCount(): Int {
        return items.size
    }
}