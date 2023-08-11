package com.sk.directudhar.ui.applyloan

data class PostCreditBeurauResponseModel(
    val Data: PostCredit,
    val DynamicData: String,
    val Msg: String,
    val Result: Boolean
)
data class PostCredit(
    val errorString: String,
    val stgOneHitId: String,
    val stgTwoHitId: String
)
