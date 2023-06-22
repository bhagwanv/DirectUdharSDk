package com.example.directudharsdk.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.sk.directudhar.BuildConfig
import com.sk.directudhar.R


class UtilsMain {
    private var context: Context? = null

    companion object UtilsObject {

        var customDialog: Dialog? = null

        var BaseUrl= "https://udhaarservice.shopkirana.in/"

        fun getToken(context: Context?): String? {
            return SharePrefsMain.getInstance(context).getString(SharePrefsMain.TOKEN)
        }

        fun getCustMobile(context: Context?): String? {
            return SharePrefsMain.getInstance(context).getString(SharePrefsMain.MOBILE_NUMBER)
                .plus("_") + SharePrefsMain.getInstance(context).getInt(SharePrefsMain.CUSTOMER_ID)
        }

        fun getHKCustomerID(context: Context?): String? {
            return SharePrefsMain.getInstance(context).getString(SharePrefsMain.HISAB_KITAB_ID) + ""
        }

        fun getDeviceUniqueID(activity: Activity): String? {
            return if (BuildConfig.DEBUG) {
                Settings.Secure.getString(
                    activity.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) + "shinoo"
            } else {
                Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
            }
        }

        fun showProgressDialog(activity: Activity) {
            if (customDialog != null) {
               customDialog!!.dismiss()
                customDialog =null
            }
            customDialog = Dialog(activity, R.style.CustomDialog)
            val mView: View = LayoutInflater.from(activity).inflate(R.layout.progress_dialog, null)
           customDialog!!.setCancelable(false)
            customDialog!!.setContentView(mView)
            if (!customDialog!!.isShowing() && !activity.isFinishing) {
                // Bad token exception handled by devendra
                try {
                   customDialog!!.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun hideProgressDialog() {
            if (customDialog != null && customDialog!!.isShowing()) {
                // Bad token exception handled by devendra
                try {
                   customDialog!!.dismiss()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun setToast(_mContext: Context?, str: String?) {
            Toast.makeText(_mContext, str, Toast.LENGTH_SHORT).show()
        }

    }

}