package com.sk.directudhar.ui.myaccount.udharStatement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.sk.directudhar.R
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.utils.Utils

class UdharStatementAdapter(
    private val items: ArrayList<UdharStatementModel>,
    private val onButtonClick: (itemId: UdharStatementModel) -> Unit
) : RecyclerView.Adapter<UdharStatementAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvItem: CardView = itemView.findViewById(R.id.cvItem)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val txnId: TextView = itemView.findViewById(R.id.tvTxnId)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val dueAmount: TextView = itemView.findViewById(R.id.tvDueAmount)
        val tvPaidAmount: TextView = itemView.findViewById(R.id.tvPaidAmount)
        val dueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val tvPaidDate: TextView = itemView.findViewById(R.id.tvPaidDate)
        val btnPayNow: AppCompatButton = itemView.findViewById(R.id.btnPayNow)
        val liBtn: LinearLayout = itemView.findViewById(R.id.liBtn)
        val liDueAmount: LinearLayout = itemView.findViewById(R.id.liDueAmount)
        val liDueDate: LinearLayout = itemView.findViewById(R.id.liDueDate)
        val liPaidDate: LinearLayout = itemView.findViewById(R.id.liPaidDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.udhar_statement_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.txnId.text = item.transactionId
        holder.status.text = item.status
        holder.date.text = Utils.simpleDateFormate(
            item.transactionDate!!,
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "dd MMMM yyyy"
        )


        if (item.status == "Paid") {
            holder.tvPaidAmount.text = item.paidAmount.toString()
            holder.liDueAmount.visibility = View.GONE
            holder.liDueDate.visibility = View.GONE
            holder.liPaidDate.visibility = View.VISIBLE
            holder.tvPaidDate.text = Utils.simpleDateFormate(
                item.paidDate!!,
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "dd MMMM yyyy"
            )
        } else {
            holder.dueAmount.text = item.dueAmount.toString()
            holder.tvPaidAmount.text = item.paidAmount.toString()
            holder.liDueAmount.visibility = View.VISIBLE
            holder.liDueDate.visibility = View.VISIBLE
            holder.liPaidDate.visibility = View.GONE
            holder.dueDate.text =
                Utils.simpleDateFormate(item.dueDate!!, "yyyy-MM-dd'T'HH:mm:ss.SSS", "dd MMMM yyyy")
        }

        if (item.isUPIEnable!!) {
            holder.liBtn.visibility = View.VISIBLE
        } else {
            holder.liBtn.visibility = View.GONE
        }

        holder.btnPayNow.setOnClickListener {
            val action =
                UdharStatementFragmentDirections.actionUdharStatementFragmentToPaymentOptionsFragment(
                    item.transactionId!!
                )
            val navController = Navigation.findNavController(it)
            navController.navigate(action)
        }

        holder.cvItem.setOnClickListener {
            onButtonClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}