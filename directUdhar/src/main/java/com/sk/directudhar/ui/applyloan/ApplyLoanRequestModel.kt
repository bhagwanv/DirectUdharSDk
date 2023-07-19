package com.sk.directudhar.ui.applyloan

data class ApplyLoanRequestModel(
    val Name: String,
    val FirmName: String,
    val Address: String,
    val MobileNo: String,
    val EmailId: String,
    val GSTNo: String,
    val Pincode: String,
    val BusinessTurnOver: String,
    val Zip: String,
    val CityId: Int,
    val StateId: Int,
    val BusinessVintage: String,
    val IsAgreementAccept: Boolean
)
