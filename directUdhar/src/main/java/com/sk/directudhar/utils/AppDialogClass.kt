package com.sk.directudhar.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import javax.inject.Inject


class AppDialogClass @Inject constructor() {

        private var continueCancelClick: OnContinueClicked? = null

        fun setOnContinueCancelClick(continueCancelClick: OnContinueClicked?) {
            this.continueCancelClick = continueCancelClick
        }
        fun accountCreatedDialog(context: Context,messageTittle:String,yesNo:String){
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage(messageTittle)
                .setCancelable(false)
                .setPositiveButton(yesNo) { dialog, id ->
                    continueCancelClick!!.onContinueClicked()
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }


    interface OnContinueClicked {
        fun onContinueClicked()
    }

}