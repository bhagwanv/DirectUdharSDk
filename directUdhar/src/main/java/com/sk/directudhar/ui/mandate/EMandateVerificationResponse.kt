package com.sk.directudhar.ui.mandate

data class EMandateVerificationResponse(
    val Msg: String,
    val Result: Boolean,
    val Data: Data
)
data class Data(
    val LeadMasterId: Int,
    val SequenceNo: Int
)