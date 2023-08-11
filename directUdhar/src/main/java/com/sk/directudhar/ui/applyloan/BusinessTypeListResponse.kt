package com.sk.directudhar.ui.applyloan

data class BusinessTypeListResponse(
    val Data: List<BusinessTypeList>,
    val DynamicData: Any,
    val Msg: String,
    val Result: Boolean
)


data class BusinessTypeList(
    val Id: Int,
    val Name: String
)