package com.sk.directudhar.ui.businessDetails

data class StatementFileResponse(
    val Msg: String,
    val Result: Boolean,
    val Data: StatementData
)
data class StatementData(
    val ImageUrl: String
)
