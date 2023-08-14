package com.sk.directudhar.ui.applyloan

import com.google.gson.annotations.SerializedName

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
class BusinessType{
    @SerializedName("PartnerName")
    var PartnerName: String?=null
    @SerializedName("PartnerNumber")
    var PartnerNumber: String?=null
}

