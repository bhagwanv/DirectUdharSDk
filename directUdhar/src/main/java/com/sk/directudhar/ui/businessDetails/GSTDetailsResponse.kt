package com.sk.directudhar.ui.businessDetails

data class GSTDetailsResponse(
    val Data: GSTDetails,
    val DynamicData: Any,
    val Msg: Any,
    val Result: Boolean
)


data class GSTDetails(
    val Name: String
)