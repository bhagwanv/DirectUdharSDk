package com.sk.directudhar.ui.businessDetails

data class BusinessDetailsResponseModel(
    val Msg: String,
    val Result: Boolean,
    val DynamicData: DynamicData
)

data class DynamicData(
    val LeadMasterId: Int,
    val SequenceNo: Int,

)