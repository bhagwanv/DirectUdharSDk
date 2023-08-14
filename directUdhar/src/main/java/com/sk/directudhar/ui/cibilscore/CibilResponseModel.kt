package com.sk.directudhar.ui.cibilscore

data class CibilResponseModel(
    val Data: CibilInfo,
    val Msg: String,
    val Result: Boolean
)
data class CibilInfo(
    val UniqueCode: String,
    val CreditLimit: Double,
    val CreditScore: Int,
    val FirstName: String,
    val LastName: String,
)
