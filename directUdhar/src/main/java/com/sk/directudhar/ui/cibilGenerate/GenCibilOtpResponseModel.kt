package com.sk.directudhar.ui.cibilGenerate

data class GenCibilOtpResponseModel(
    val Data: Data,
    val Msg: String,
    val Result: Boolean
)

data class Data(
    val errorString: String,
    val stgOneHitId: String,
    val stgTwoHitId: String
)