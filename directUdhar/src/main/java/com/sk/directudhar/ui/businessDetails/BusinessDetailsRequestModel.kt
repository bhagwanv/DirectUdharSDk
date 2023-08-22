package com.sk.directudhar.ui.businessDetails

data class BusinessDetailsRequestModel(
    val LeadId: Int,
    val GSTNo: String,
    val BusinessTypeId: Int,
    val BusinessName: String,
    val Partners: ArrayList<BusinessType>,
    val BusinessTurnOver: String,
    val BusinessIncorporationDate: String,
    val IncomSlab: String,
    val ownershipType: String,
    val DocumentNumber: String,
    val ElectricityBillUrl: String,
    val BankPassBookUrl: String,
)

data class BusinessType(val PartnerName: String, val PartnerNumber: String)
