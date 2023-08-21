package com.sk.directudhar.ui.cibilGenerate

data class CibilGetOTPRequestModel(
    val LeadMasterId: Int,
    val mobileNo: String,
    val stgOneHitId: String,
    val stgTwoHitId: String,
)
