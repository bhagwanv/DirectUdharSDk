package com.sk.directudhar.utils

import android.content.Context
import android.widget.Toast

class Utils(private var context: Context) {

    companion object {
        val BASE_URL = "https://udhaarservice.shopkirana.in/"
        var CLIENT_CREDENTIALS = "client_credentials"
        var SECRETKEY = "b02013e9-b92b-4563-a330-aec123bf13d7"
        var APIKYYE = "e57f97e0-46ea-4be0-9fdf-c92b410cf022"

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
    }
}