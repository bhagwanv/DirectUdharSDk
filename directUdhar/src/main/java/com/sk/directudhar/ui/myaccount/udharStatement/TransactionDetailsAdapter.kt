package com.sk.directudhar.ui.myaccount.udharStatement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sk.directudhar.R

class TransactionDetailsAdapter(private val dataList: ArrayList<TransactionDetailResponseModel>) :
    RecyclerView.Adapter<TransactionDetailsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTitleValue: TextView = itemView.findViewById(R.id.tvTitleValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_details_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.tvTitle.text = "${currentItem.TrasanctionType} : "
        holder.tvTitleValue.text = "${currentItem.TxnAmount} /-"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}