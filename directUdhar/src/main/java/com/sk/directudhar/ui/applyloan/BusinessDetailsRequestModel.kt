package com.sk.directudhar.ui.applyloan

data class BusinessDetailsRequestModel(
    val LeadId: Int,
    val GSTNo: String,
    val BusinessName: String,
    val BusinessType: ArrayList<BusinessType>,
    val BusinessTurnOver: Int,
    val BusinessIncorporationDate: String,
    val IncomSlab: String,
    val ownershipType: String,
)
class BusinessType(
    val PartnerName: String,
    val PartnerNumber: Int
)
