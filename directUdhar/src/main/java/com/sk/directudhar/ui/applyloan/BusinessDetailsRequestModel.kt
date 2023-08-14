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
data class BusinessType(val PartnerName:String,val PartnerNumber:String)

/*class BusinessType{
    @SerializedName("PartnerName")
    var PartnerName=""
    @SerializedName("PartnerNumber")
    var PartnerNumber = 0
    constructor(PartnerName:String){
        this.PartnerName = PartnerName
    }
    constructor(PartnerNumber:Int){
        this.PartnerNumber = PartnerNumber
    }

    constructor(toString: String, toInt: Int){
        this.PartnerName = toString
        this.PartnerNumber = toInt
    }
}*/