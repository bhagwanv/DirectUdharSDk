package com.sk.directudhar.utils

import android.content.Context
import android.widget.Toast



class Utils(private var context: Context) {

    companion object {
        val BASE_URL ="https://udhaarservice.shopkirana.in/"
        var  CLIENT_CREDENTIALS ="client_credentials"
        var  SECRETKEY ="b02013e9-b92b-4563-a330-aec123bf13d7"
        var  APIKYYE ="e57f97e0-46ea-4be0-9fdf-c92b410cf022"
        var  AADHAAR_VALIDATE_SUCCESSFULLY ="AADHAAR_VALIDATE_SUCCESSFULLY"
        var cameraRequest = 1888
        var  SuccessType ="success_type_al"
        val vintageList =
            arrayOf<String>("Select Business Vintage", "Less than 2 years", "Greater Than 2 Years")

        var APPLY_LOAN_POLICY="I Authorize Direct Udhar and its representatives " +
                "to SMS,Call or commpunicate via Whatsup regarding my application " +
                "and other applicable offers.This consent overrides any registratio" +
                "n for DNC/NDNC.I confirm I am in India,I am a major and a resident\n" +
                "of India and I have read I accept Indusland Bank's"

        var PROCESS_TEXT="By Clicking Apply now i agree to the terms and  condition " +
                "and authorize Direct Udhar to send promotional communication to me "

        fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

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