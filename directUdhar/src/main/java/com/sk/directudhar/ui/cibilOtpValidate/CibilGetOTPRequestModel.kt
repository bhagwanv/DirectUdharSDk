package com.sk.directudhar.ui.cibilOtpValidate

data class CibilGetOTPRequestModel(
    val LeadMasterId: Int,
    val mobileNo: String,
    val stgOneHitId: String,
    val stgTwoHitId: String,
)
