package com.sk.directudhar.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.sk.directudhar.R
import javax.inject.Inject


class AppDialogClass @Inject constructor() {

        private var continueCancelClick: OnContinueClicked? = null

        fun setOnContinueCancelClick(continueCancelClick: OnContinueClicked?) {
            this.continueCancelClick = continueCancelClick
        }
        fun alertDialog(context: Context,messageTittle:String,yesNo:String){
            val dialog = Dialog(context)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_alert_msg)
            dialog.setCanceledOnTouchOutside(false)
            val tvMsg = dialog.findViewById<TextView>(R.id.tvMsg)
            val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnOk)
            tvMsg.text = messageTittle
            btnOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    fun termsAndAgreementDialog(context: Context,messageTittle:String,yesNo:String){
        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_alert_msg)
        dialog.setCanceledOnTouchOutside(false)
        val tvMsg = dialog.findViewById<TextView>(R.id.tvMsg)
        val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnOk)
        tvMsg.text = messageTittle
        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    interface OnContinueClicked {
        fun onContinueClicked()
    }

}