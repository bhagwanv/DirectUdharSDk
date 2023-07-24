package com.sk.directudhar.ui.cibilscore

data class CibilRequestModel(
    val FirstName: String,
    val LastName: String,
    val FlatNo: String,
    val PanNumber: String,
    val PinCode: String,
    val StateCode: Int,
    val City: String,
    val dateOfBirth:String,
    val Gender:String,
    val EmailId:String,
    val MobileNo:String,
    val IsAcceptConsent: Boolean,
    val LeadMasterId:Int
)
