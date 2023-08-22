package com.sk.directudhar.ui.cibilGenerate

data class CibilOTPVerifyRequestModel(
    val LeadMasterId: Int,
    val stgOneHitId: String,
    val stgTwoHitId: String,
    val otp: String,
)
