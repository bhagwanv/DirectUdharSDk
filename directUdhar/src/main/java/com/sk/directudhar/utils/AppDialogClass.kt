package com.sk.directudhar.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Html
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.sk.directudhar.R
import com.sk.directudhar.databinding.DialogAlertMsgBinding
import com.sk.directudhar.databinding.DialogTermsConditionBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import javax.inject.Inject


class AppDialogClass @Inject constructor() {

    private var continueCancelClick: OnContinueClicked? = null

    fun setOnContinueCancelClick(continueCancelClick: OnContinueClicked?) {
        this.continueCancelClick = continueCancelClick
    }

    fun alertDialog(context: Context, messageTittle: String, yesNo: String) {
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

    fun termsAndAgreementDialog(context: Context, messageTittle: String) {
        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_terms_condition)
        dialog.setCanceledOnTouchOutside(false)
        //val tvMsg = dialog.findViewById<TextView>(R.id.tvMsg)
        val btnAgree = dialog.findViewById<AppCompatButton>(R.id.btnAgree)
        val cancel = dialog.findViewById<AppCompatTextView>(R.id.tvCancel)
        val WvUrl = dialog.findViewById<WebView>(R.id.WvUrl)
        WvUrl.loadUrl(messageTittle)
        btnAgree.setOnClickListener {
            continueCancelClick!!.onContinueClicked(true)
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            continueCancelClick!!.onContinueClicked(false)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun termsAndAgreementPopUp(activity: MainActivitySDk, messageTittle: String) {
        val inflater = activity.layoutInflater
        val mBinding: DialogTermsConditionBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_terms_condition, null, false)
        val customDialog = Dialog(activity, R.style.CustomDialog)
        customDialog.setCancelable(false)
        customDialog.setContentView(mBinding.root)
        mBinding.WvUrl.loadUrl(messageTittle)
        mBinding.WvUrl.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                mBinding.pBar.visibility = View.GONE
            }

        }
        mBinding.btnAgree.setOnClickListener {
            continueCancelClick!!.onContinueClicked(true)
            customDialog.dismiss()
        }
        mBinding.tvCancel.setOnClickListener {
            continueCancelClick!!.onContinueClicked(false)
            customDialog.dismiss()
        }
        customDialog.show()
    }

    interface OnContinueClicked {
        fun onContinueClicked(isAgree: Boolean)
    }

}