package com.sk.directudhar.ui.cibilscore

data class CibilActivityCompleteResponseModel(
    val Data: CibilActivityComplete,
    val Msg: String,
    val Result: Boolean
)
data class CibilActivityComplete(
    val LeadMasterId: Int,
    val SequenceNo: Int
)

