package com.sk.directudhar.ui.personalDetails

data class PostCreditBeurauResponseModel(
    val Data: PostCredit,
    val DynamicData: DynamicData,
    val Msg: String,
    val Result: Boolean
)

data class PostCredit(
    val errorString: String,
    val stgOneHitId: String,
    val stgTwoHitId: String
)

data class DynamicData(
    val LeadMasterId: Int,
    val SequenceNo: Int
)
