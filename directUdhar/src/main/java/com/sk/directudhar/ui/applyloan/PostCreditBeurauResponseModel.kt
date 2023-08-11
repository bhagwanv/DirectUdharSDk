package com.sk.directudhar.ui.applyloan

data class PostCreditBeurauResponseModel(
    val Data: Data1,
    val DynamicData: Any,
    val Msg: String,
    val Result: Boolean
)
data class Data1(
    val errorString: Any,
    val stgOneHitId: String,
    val stgTwoHitId: String
)
