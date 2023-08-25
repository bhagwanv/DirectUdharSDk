package com.sk.directudhar.ui.mandate

data class EMandateAddRequestModel(
    val LeadMasterId :Int,
    val BankName: String,
    val IFSCCode: String,
    val AccountNumber: String,
    val AccountType:String,
    val Channel:String
)
