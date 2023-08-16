package com.sk.directudhar.ui.mandate

data class EMandateAddRequestModel(
    val LeadMasterId :Int,
    val BankName: String,
    val IfscCode: String,
    val AccountType:String,
    val ChannelType:String
)