package com.sk.directudhar.ui.cibilGenerate

data class CibilGenerateRequestModel(
    val LeadMasterId: Int,
    val mobileNo: String,
    val stgOneHitId: String,
    val stgTwoHitId: String,
    val otp: String,
)
