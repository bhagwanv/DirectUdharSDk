package com.sk.directudhar.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

class Utils(private var context: Context) {

    companion object {
        val BASE_URL = "https://udhaarservice.shopkirana.in/"
        var CLIENT_CREDENTIALS = "client_credentials"
        var SECRETKEY = "88662f90-43c2-5fc5-6d77-9b80f1bf3337"
        var APIKYYE = "95f883fa-b4df-0cff-ce1b-f7608f88f075"
        var AADHAAR_VALIDATE_SUCCESSFULLY = "AADHAAR_VALIDATE_SUCCESSFULLY"
        var BUSINESS_VALIDATE_SUCCESSFULLY = "BUSINESS_VALIDATE_SUCCESSFULLY"
        var AADHAAR_OTP_VALIDATE_SUCCESSFULLY = "AADHAAR_OTP_VALIDATE_SUCCESSFULLY"
        var cameraRequest = 1888
        var WRITE_PERMISSION = 0x01
        var FILE_NAME = "fileName"
        var IS_GALLERY_OPTION = "isGalleryOption"
        var FILE_PATH = "filePath"
        var SuccessType = "success_type_al"
        val vintageList = arrayOf<String>("Select Business Vintage", "Less than 2 years", "Greater Than 2 Years")
        val accountTypeList = arrayOf<String>(" Select Account Type", "Saving", "Current")
        val channelList = arrayOf<String>("Select Channel Type", "Net", "Debit")

        var APPLY_LOAN_POLICY = "I Authorize Direct Udhar and its representatives " +
                "to SMS,Call or commpunicate via Whatsup regarding my application " +
                "and other applicable offers.This consent overrides any registratio" +
                "n for DNC/NDNC.I confirm I am in India,I am a major and a resident\n" +
                "of India and I have read I accept Indusland Bank's"

        var PROCESS_TEXT = "By Clicking Apply now i agree to the terms and  condition " +
                "and authorize Direct Udhar to send promotional communication to me "
        var  EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY ="EAGREEMENT_OTP_VALIDATE_SUCCESSFULLY"
        val countdownDuration: Long = 50000

        fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        fun isValidAadhaar(aadhaarNumber: String): Boolean {
            val cleanAadhaarNumber = aadhaarNumber.replace("\\s".toRegex(), "")
            if (cleanAadhaarNumber.length != 12) {
                return false
            }

            if (!cleanAadhaarNumber.matches("\\d+".toRegex())) {
                return false
            }

            val firstDigit = cleanAadhaarNumber[0].toString().toInt()
            if (firstDigit < 1 || firstDigit > 9) {
                return false
            }
            return true
        }

        fun getPath(context: Context, uri: Uri?): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor =
                context.getContentResolver().query(uri!!, projection, null, null, null)
                    ?: return null
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val s = cursor.getString(column_index)
            cursor.close()
            return s
        }

        fun simpleDateFormate(inputDate: String, inputFormat: String, outputFormat: String): String? {
            return try {
                val inputFormats = SimpleDateFormat(inputFormat, Locale.getDefault())
                val outputFormats = SimpleDateFormat(outputFormat, Locale.getDefault())
                val date = inputFormats.parse(inputDate)
                return outputFormats.format(date!!)
            } catch (e: java.text.ParseException) {
                try {
                    val inputFormats = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormats = SimpleDateFormat(outputFormat, Locale.getDefault())
                    val date = inputFormats.parse(inputDate)
                    return outputFormats.format(date!!)
                } catch (e: java.text.ParseException) {
                    e.printStackTrace()
                    return ""
                }
            }
        }
    }
}