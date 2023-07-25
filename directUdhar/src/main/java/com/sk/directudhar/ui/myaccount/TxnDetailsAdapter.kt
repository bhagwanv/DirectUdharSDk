package com.sk.directudhar.ui.myaccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sk.directudhar.R
import com.sk.directudhar.utils.Utils

class TxnDetailsAdapter(private val items: ArrayList<UdharStatementModel>) : RecyclerView.Adapter<TxnDetailsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val txnId: TextView = itemView.findViewById(R.id.tvTxnId)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val dueAmount: TextView = itemView.findViewById(R.id.tvDueAmount)
        val dueDate: TextView = itemView.findViewById(R.id.tvDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.txn_details_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = Utils.simpleDateFormate(item.TransactionDate!!, "dd MMMM yyyy")
        holder.txnId.text = item.TrasanctionId
        holder.status.text = item.status
        holder.dueAmount.text = item.DueAmount.toString()
        holder.dueDate.text = item.DueDate
    }

    override fun getItemCount(): Int {
        return items.size
    }
}