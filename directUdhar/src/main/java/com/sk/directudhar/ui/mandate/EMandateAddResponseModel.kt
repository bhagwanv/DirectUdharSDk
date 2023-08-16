package com.sk.directudhar.ui.mandate

data class EMandateAddResponseModel(
    val Msg: String,
    val Result: Boolean,
    val Data: Data
)

class Data(val LeadMasterId: Int, val SequenceNo: Int) {
}