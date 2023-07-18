package com.sk.directudhar.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.sk.directudhar.R

class ProgressDialog {
    private var dialog: Dialog? = null
    fun show(context: Context?) {
        if (dialog != null && dialog!!.isShowing) {
            return
        }
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
        if (dialog != null) {
            dialog!!.show()
        }
    }

    fun dismiss() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    companion object {
        private var mInstance: ProgressDialog? = null

        @get:Synchronized
        val instance: ProgressDialog?
            get() {
                if (mInstance == null) {
                    mInstance = ProgressDialog()
                }
                return mInstance
            }
    }
}