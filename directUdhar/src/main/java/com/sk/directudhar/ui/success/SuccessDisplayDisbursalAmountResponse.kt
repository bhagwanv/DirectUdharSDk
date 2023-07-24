package com.sk.directudhar.ui.success

data class SuccessDisplayDisbursalAmountResponse(
    val ConvenionFeeRate: Double,
    val CreatedDate: String,
    val CreditLimit: Double,
    val GSTAmount: Double,
    val LeadNo: String,
    val ProcessingFee: Double,
    val ProcessingFeeAmount: Double,
    val ProcessingFeeTax: Double,
    val Status: Int
)