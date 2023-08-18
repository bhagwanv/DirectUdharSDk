package com.sk.directudhar.ui.cibilOtpValidate

data class CibilOTPVerifyRequestModel(
    val LeadMasterId: Int,
    val mobileNo: String,
    val stgOneHitId: String,
    val stgTwoHitId: String,
    val otp: String,
)
